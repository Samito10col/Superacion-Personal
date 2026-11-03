package com.superacion.backend.service;

import com.superacion.backend.dto.MultaResponse;
import com.superacion.backend.dto.ResumenMultasResponse;
import com.superacion.backend.exception.AccesoDenegadoException;
import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.exception.ReglaNegocioException;
import com.superacion.backend.model.EstadoMulta;
import com.superacion.backend.model.EstadoPago;
import com.superacion.backend.model.Multa;
import com.superacion.backend.repository.MultaRepository;
import com.superacion.backend.repository.PagoRepository;
import com.superacion.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MultaService {

    private final MultaRepository multaRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccesoGrupo accesoGrupo;

    public MultaService(MultaRepository multaRepository,
                        PagoRepository pagoRepository,
                        UsuarioRepository usuarioRepository,
                        AccesoGrupo accesoGrupo) {
        this.multaRepository = multaRepository;
        this.pagoRepository = pagoRepository;
        this.usuarioRepository = usuarioRepository;
        this.accesoGrupo = accesoGrupo;
    }

    @Transactional(readOnly = true)
    public ResumenMultasResponse misMultas(Long usuarioId) {
        return ResumenMultasResponse.de(
                multaRepository.findByAsignacionUsuarioIdOrderByPeriodoInicioDescIdDesc(usuarioId).stream()
                        .map(MultaResponse::desde)
                        .toList());
    }

    @Transactional(readOnly = true)
    public ResumenMultasResponse delGrupo(Long grupoId, Long usuarioId) {
        accesoGrupo.exigirMiembro(grupoId, usuarioId);
        return ResumenMultasResponse.de(
                multaRepository.findByAsignacionHabitoGrupoIdOrderByPeriodoInicioDescIdDesc(grupoId).stream()
                        .map(MultaResponse::desde)
                        .toList());
    }

    // RN-17: un admin puede anular una multa con justificación (enfermedad, viaje...)
    @Transactional
    public MultaResponse anular(Long multaId, Long usuarioId, String motivo) {
        Multa multa = multaRepository.findById(multaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Multa no encontrada"));

        accesoGrupo.exigirAdmin(multa.getGrupoId(), usuarioId);

        if (multa.getEstado() != EstadoMulta.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden anular multas pendientes (estado actual: "
                    + multa.getEstado() + ")");
        }
        if (multa.perteneceA(usuarioId)) {
            throw new AccesoDenegadoException("No puedes anular tus propias multas; debe hacerlo otro administrador");
        }
        if (pagoRepository.existsByMultaIdAndEstadoIn(multaId, List.of(EstadoPago.POR_CONFIRMAR, EstadoPago.CONFIRMADO))) {
            throw new ReglaNegocioException("Esta multa tiene pagos registrados; revisa esos pagos antes de anularla");
        }

        multa.anular(usuarioRepository.getReferenceById(usuarioId), motivo.trim());
        return MultaResponse.desde(multa);
    }
}