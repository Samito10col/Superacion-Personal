package com.superacion.backend.service;

import com.superacion.backend.dto.FondoResponse;
import com.superacion.backend.dto.PagoResponse;
import com.superacion.backend.dto.RegistrarPagoRequest;
import com.superacion.backend.exception.AccesoDenegadoException;
import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.exception.ReglaNegocioException;
import com.superacion.backend.model.*;
import com.superacion.backend.repository.MultaRepository;
import com.superacion.backend.repository.PagoRepository;
import com.superacion.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagoService {

    private static final List<EstadoPago> PAGOS_VIGENTES = List.of(EstadoPago.POR_CONFIRMAR, EstadoPago.CONFIRMADO);

    private final PagoRepository pagoRepository;
    private final MultaRepository multaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccesoGrupo accesoGrupo;

    public PagoService(PagoRepository pagoRepository,
                       MultaRepository multaRepository,
                       UsuarioRepository usuarioRepository,
                       AccesoGrupo accesoGrupo) {
        this.pagoRepository = pagoRepository;
        this.multaRepository = multaRepository;
        this.usuarioRepository = usuarioRepository;
        this.accesoGrupo = accesoGrupo;
    }

    // RN-21: el deudor registra su pago
    @Transactional
    public PagoResponse registrar(Long multaId, Long usuarioId, RegistrarPagoRequest request) {
        Multa multa = multaRepository.findById(multaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Multa no encontrada"));

        if (!multa.perteneceA(usuarioId)) {
            throw new AccesoDenegadoException("Solo puedes registrar pagos de tus propias multas");
        }
        if (multa.getEstado() != EstadoMulta.PENDIENTE) {
            throw new ReglaNegocioException("Esta multa no está pendiente de pago (estado: " + multa.getEstado() + ")");
        }

        // RN-20: pagos parciales permitidos, pero nunca más de lo que se debe
        long comprometido = pagoRepository.sumarPorMultaYEstados(multaId, PAGOS_VIGENTES);
        long saldo = multa.getMonto() - comprometido;
        if (saldo <= 0) {
            throw new ReglaNegocioException("Ya registraste pagos por el total; espera a que el administrador los confirme");
        }
        if (request.monto() > saldo) {
            throw new ReglaNegocioException("El monto supera lo que falta por pagar ($" + saldo + ")");
        }

        String referencia = request.referencia() == null || request.referencia().isBlank()
                ? null : request.referencia().trim();

        Pago pago = pagoRepository.save(new Pago(multa, request.monto(), request.metodo(),
                referencia, usuarioRepository.getReferenceById(usuarioId)));
        return PagoResponse.desde(pago);
    }

    @Transactional
    public PagoResponse confirmar(Long pagoId, Long usuarioId) {
        Pago pago = buscarPorConfirmar(pagoId, usuarioId);
        Multa multa = pago.getMulta();

        pago.confirmar(usuarioRepository.getReferenceById(usuarioId));

        // RN-20: si los pagos confirmados ya cubren el monto, la multa queda pagada
        long confirmado = pagoRepository.sumarPorMultaYEstados(multa.getId(), List.of(EstadoPago.CONFIRMADO));
        if (confirmado >= multa.getMonto()) {
            multa.marcarPagada();
        }
        return PagoResponse.desde(pago);
    }

    @Transactional
    public PagoResponse rechazar(Long pagoId, Long usuarioId, String motivo) {
        Pago pago = buscarPorConfirmar(pagoId, usuarioId);
        pago.rechazar(usuarioRepository.getReferenceById(usuarioId), motivo.trim());
        return PagoResponse.desde(pago);
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> delGrupo(Long grupoId, Long usuarioId) {
        accesoGrupo.exigirMiembro(grupoId, usuarioId);
        return pagoRepository.findByMultaAsignacionHabitoGrupoIdOrderByCreadoEnDesc(grupoId).stream()
                .map(PagoResponse::desde)
                .toList();
    }

    // RN-22: el fondo común es visible para todos los miembros
    @Transactional(readOnly = true)
    public FondoResponse fondo(Long grupoId, Long usuarioId) {
        accesoGrupo.exigirMiembro(grupoId, usuarioId);

        long recaudado = pagoRepository.sumarPorGrupoYEstado(grupoId, EstadoPago.CONFIRMADO);
        long multasPendientes = multaRepository.sumarPorGrupoYEstado(grupoId, EstadoMulta.PENDIENTE);
        long abonosAPendientes = pagoRepository.sumarAbonosDeMultasEnEstado(
                grupoId, EstadoPago.CONFIRMADO, EstadoMulta.PENDIENTE);
        long porConfirmar = pagoRepository.countByMultaAsignacionHabitoGrupoIdAndEstado(grupoId, EstadoPago.POR_CONFIRMAR);

        return new FondoResponse(
                grupoId,
                recaudado,
                multasPendientes - abonosAPendientes,
                porConfirmar,
                delGrupo(grupoId, usuarioId)
        );
    }

    // RN-21: solo un admin revisa pagos, y nunca los de sus propias multas
    private Pago buscarPorConfirmar(Long pagoId, Long usuarioId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

        Multa multa = pago.getMulta();
        accesoGrupo.exigirAdmin(multa.getGrupoId(), usuarioId);

        if (pago.getEstado() != EstadoPago.POR_CONFIRMAR) {
            throw new ReglaNegocioException("Este pago ya fue revisado (estado: " + pago.getEstado() + ")");
        }
        if (multa.perteneceA(usuarioId)) {
            throw new AccesoDenegadoException("No puedes revisar pagos de tus propias multas; debe hacerlo otro administrador");
        }
        return pago;
    }
}