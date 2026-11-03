package com.superacion.backend.service;

import com.superacion.backend.config.ZonaHoraria;
import com.superacion.backend.dto.RegistroResponse;
import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.exception.ReglaNegocioException;
import com.superacion.backend.model.*;
import com.superacion.backend.repository.AsignacionRepository;
import com.superacion.backend.repository.HabitoRepository;
import com.superacion.backend.repository.RegistroRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistroService {

    private static final int MAX_NOTA = 500;

    private final RegistroRepository registroRepository;
    private final AsignacionRepository asignacionRepository;
    private final HabitoRepository habitoRepository;
    private final AccesoGrupo accesoGrupo;
    private final AlmacenamientoEvidencias almacenamiento;

    public RegistroService(RegistroRepository registroRepository,
                           AsignacionRepository asignacionRepository,
                           HabitoRepository habitoRepository,
                           AccesoGrupo accesoGrupo,
                           AlmacenamientoEvidencias almacenamiento) {
        this.registroRepository = registroRepository;
        this.asignacionRepository = asignacionRepository;
        this.habitoRepository = habitoRepository;
        this.accesoGrupo = accesoGrupo;
        this.almacenamiento = almacenamiento;
    }

    @Transactional
    public RegistroResponse registrar(Long habitoId, Long usuarioId, LocalDate fecha,
                                      String nota, MultipartFile evidencia) {
        Asignacion asignacion = asignacionRepository.findByHabitoIdAndUsuarioId(habitoId, usuarioId)
                .filter(Asignacion::isActiva)
                .filter(a -> a.getHabito().isActivo())
                .orElseThrow(() -> new RecursoNoEncontradoException("No tienes asignado este hábito"));

        Habito habito = asignacion.getHabito();

        // RN-08: solo hoy, o ayer como registro tardío
        LocalDate hoy = ZonaHoraria.hoy();
        LocalDate dia = fecha == null ? hoy : fecha;
        if (dia.isAfter(hoy)) {
            throw new ReglaNegocioException("No puedes registrar días futuros");
        }
        if (dia.isBefore(hoy.minusDays(1))) {
            throw new ReglaNegocioException("Solo puedes registrar hoy o ayer (máximo un día de retraso)");
        }

        // RN-09: máximo un registro por día...
        if (registroRepository.existsByAsignacionIdAndFecha(asignacion.getId(), dia)) {
            throw new ReglaNegocioException("Ya registraste este hábito el " + dia);
        }

        // ...y no más de la meta del periodo (los rechazados no cuentan)
        Frecuencia frecuencia = habito.getFrecuencia();
        long cumplidos = registroRepository.countByAsignacionIdAndFechaBetweenAndEstadoNot(
                asignacion.getId(),
                frecuencia.inicioDelPeriodo(dia),
                frecuencia.finDelPeriodo(dia),
                EstadoRegistro.RECHAZADO);
        if (cumplidos >= habito.getMetaPorPeriodo()) {
            throw new ReglaNegocioException("Ya cumpliste la meta de este periodo ("
                    + habito.getMetaPorPeriodo() + " veces)");
        }

        String notaLimpia = (nota == null || nota.isBlank()) ? null : nota.trim();
        if (notaLimpia != null && notaLimpia.length() > MAX_NOTA) {
            throw new ReglaNegocioException("La nota no puede superar " + MAX_NOTA + " caracteres");
        }

        ModoVerificacion modo = habito.getModoVerificacion();
        boolean hayFoto = evidencia != null && !evidencia.isEmpty();
        if (modo.requiereEvidencia() && !hayFoto) {
            throw new ReglaNegocioException("Este hábito exige una foto como evidencia");
        }

        String rutaFoto = hayFoto ? almacenamiento.guardar(evidencia) : null;

        EstadoRegistro estado = modo.requiereValidacion()
                ? EstadoRegistro.PENDIENTE_VALIDACION
                : EstadoRegistro.APROBADO;

        Registro registro = registroRepository.save(new Registro(asignacion, dia, estado, notaLimpia, rutaFoto));
        return RegistroResponse.desde(registro);
    }

    @Transactional(readOnly = true)
    public List<RegistroResponse> listarPeriodoActual(Long habitoId, Long usuarioId) {
        Habito habito = habitoRepository.findById(habitoId)
                .filter(Habito::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Hábito no encontrado"));

        accesoGrupo.exigirMiembro(habito.getGrupo().getId(), usuarioId);

        LocalDate hoy = ZonaHoraria.hoy();
        Frecuencia frecuencia = habito.getFrecuencia();

        return registroRepository.findByAsignacionHabitoIdAndFechaBetweenOrderByFechaDescCreadoEnDesc(
                        habitoId, frecuencia.inicioDelPeriodo(hoy), frecuencia.finDelPeriodo(hoy))
                .stream()
                .map(RegistroResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public Resource evidencia(Long registroId, Long usuarioId) {
        Registro registro = registroRepository.findById(registroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro no encontrado"));

        // Solo los miembros del grupo pueden ver la foto
        accesoGrupo.exigirMiembro(registro.getAsignacion().getHabito().getGrupo().getId(), usuarioId);

        if (!registro.tieneEvidencia()) {
            throw new RecursoNoEncontradoException("Este registro no tiene evidencia");
        }
        return almacenamiento.cargar(registro.getEvidenciaRuta());
    }
}