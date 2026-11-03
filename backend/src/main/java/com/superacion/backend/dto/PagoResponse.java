package com.superacion.backend.dto;

import com.superacion.backend.model.EstadoPago;
import com.superacion.backend.model.MetodoPago;
import com.superacion.backend.model.Multa;
import com.superacion.backend.model.Pago;

import java.time.Instant;

public record PagoResponse(
        Long id,
        Long multaId,
        Long usuarioId,
        String usuarioNombre,
        String habitoNombre,
        int monto,
        MetodoPago metodo,
        String referencia,
        EstadoPago estado,
        String motivoRechazo,
        Instant creadoEn,
        Instant revisadoEn
) {

    public static PagoResponse desde(Pago pago) {
        Multa multa = pago.getMulta();
        return new PagoResponse(
                pago.getId(),
                multa.getId(),
                multa.getAsignacion().getUsuario().getId(),
                multa.getAsignacion().getUsuario().getNombre(),
                multa.getAsignacion().getHabito().getNombre(),
                pago.getMonto(),
                pago.getMetodo(),
                pago.getReferencia(),
                pago.getEstado(),
                pago.getMotivoRechazo(),
                pago.getCreadoEn(),
                pago.getRevisadoEn()
        );
    }
}