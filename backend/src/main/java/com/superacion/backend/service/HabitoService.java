package com.superacion.backend.service;

import com.superacion.backend.config.ZonaHoraria;
import com.superacion.backend.dto.*;
import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.exception.ReglaNegocioException;
import com.superacion.backend.model.*;
import com.superacion.backend.repository.AsignacionRepository;
import com.superacion.backend.repository.HabitoRepository;
import com.superacion.backend.repository.MiembroGrupoRepository;
import com.superacion.backend.repository.RegistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HabitoService {

    private final HabitoRepository habitoRepository;
    private final AsignacionRepository asignacionRepository;
    private final MiembroGrupoRepository miembroGrupoRepository;
    private final RegistroRepository registroRepository;
    private final AccesoGrupo accesoGrupo;

    public HabitoService(HabitoRepository habitoRepository,
                         AsignacionRepository asignacionRepository,
                         MiembroGrupoRepository miembroGrupoRepository,
                         RegistroRepository registroRepository,
                         AccesoGrupo accesoGrupo) {
        this.habitoRepository = habitoRepository;
        this.asignacionRepository = asignacionRepository;
        this.miembroGrupoRepository = miembroGrupoRepository;
        this.registroRepository = registroRepository;
        this.accesoGrupo = accesoGrupo;
    }

    @Transactional
    public HabitoResponse crear(Long grupoId, Long usuarioId, CrearHabitoRequest request) {
        MiembroGrupo admin = accesoGrupo.exigirAdmin(grupoId, usuarioId);

        // Las fotos de evidencia están desactivadas por ahora (no tenemos almacenamiento en la nube)
        if (request.modoVerificacion().requiereEvidencia()) {
            throw new ReglaNegocioException("Las fotos de evidencia no están habilitadas. Usa AUTODECLARADO o VALIDACION_PAR");
        }

        Frecuencia frecuencia = request.frecuencia();
        if (request.metaPorPeriodo() > frecuencia.metaMaxima()) {
            throw new ReglaNegocioException("Para un hábito " + frecuencia.name().toLowerCase()
                    + " la meta máxima es " + frecuencia.metaMaxima());
        }

        String descripcion = request.descripcion() == null || request.descripcion().isBlank()
                ? null : request.descripcion().trim();

        Habito habito = habitoRepository.save(new Habito(
                admin.getGrupo(),
                request.nombre().trim(),
                descripcion,
                frecuencia,
                request.metaPorPeriodo(),
                request.tipoMulta(),
                request.valorMulta(),
                request.modoVerificacion(),
                admin.getUsuario()
        ));

        List<AsignadoResponse> asignados = List.of();
        if (!Boolean.FALSE.equals(request.asignarATodos())) {
            // Las obligaciones empiezan en el siguiente periodo completo, para que sea justo con todos
            LocalDate desde = frecuencia.inicioDelSiguientePeriodo(ZonaHoraria.hoy());
            asignados = miembroGrupoRepository.findByGrupoIdAndActivoTrueOrderByUnidoEnAsc(grupoId).stream()
                    .map(miembro -> asignacionRepository.save(new Asignacion(habito, miembro.getUsuario(), desde)))
                    .map(AsignadoResponse::desde)
                    .toList();
        }

        return HabitoResponse.desde(habito, asignados);
    }

    @Transactional(readOnly = true)
    public List<HabitoResponse> listarDelGrupo(Long grupoId, Long usuarioId) {
        accesoGrupo.exigirMiembro(grupoId, usuarioId);

        return habitoRepository.findByGrupoIdAndActivoTrueOrderByCreadoEnAsc(grupoId).stream()
                .map(habito -> HabitoResponse.desde(habito, asignadosDe(habito.getId())))
                .toList();
    }

    @Transactional
    public AsignadoResponse asignar(Long habitoId, Long usuarioId, AsignarHabitoRequest request) {
        Habito habito = habitoRepository.findById(habitoId)
                .filter(Habito::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Hábito no encontrado"));

        Long grupoId = habito.getGrupo().getId();
        accesoGrupo.exigirAdmin(grupoId, usuarioId);

        MiembroGrupo destino = accesoGrupo.buscarMiembro(grupoId, request.usuarioId())
                .orElseThrow(() -> new ReglaNegocioException("Esa persona no es miembro del grupo"));

        // RN-07: las obligaciones empiezan en el siguiente periodo completo
        LocalDate desde = habito.getFrecuencia().inicioDelSiguientePeriodo(ZonaHoraria.hoy());

        Optional<Asignacion> existente = asignacionRepository.findByHabitoIdAndUsuarioId(habitoId, request.usuarioId());
        Asignacion asignacion;
        if (existente.isPresent()) {
            asignacion = existente.get();
            if (asignacion.isActiva()) {
                throw new ReglaNegocioException("Esa persona ya tiene asignado este hábito");
            }
            asignacion.reactivar(desde);
        } else {
            asignacion = asignacionRepository.save(new Asignacion(habito, destino.getUsuario(), desde));
        }

        return AsignadoResponse.desde(asignacion);
    }

    @Transactional(readOnly = true)
    public List<MiHabitoResponse> misHabitos(Long usuarioId) {
        LocalDate hoy = ZonaHoraria.hoy();
        return asignacionRepository.findByUsuarioIdAndActivaTrue(usuarioId).stream()
                .filter(asignacion -> asignacion.getHabito().isActivo())
                .map(asignacion -> {
                    Frecuencia frecuencia = asignacion.getHabito().getFrecuencia();
                    int cumplidos = (int) registroRepository.countByAsignacionIdAndFechaBetweenAndEstadoNot(
                            asignacion.getId(),
                            frecuencia.inicioDelPeriodo(hoy),
                            frecuencia.finDelPeriodo(hoy),
                            EstadoRegistro.RECHAZADO);
                    boolean registradoHoy = registroRepository.existsByAsignacionIdAndFecha(asignacion.getId(), hoy);
                    return MiHabitoResponse.desde(asignacion, hoy, cumplidos, registradoHoy);
                })
                .toList();
    }

    private List<AsignadoResponse> asignadosDe(Long habitoId) {
        return asignacionRepository.findByHabitoIdAndActivaTrue(habitoId).stream()
                .map(AsignadoResponse::desde)
                .toList();
    }
}