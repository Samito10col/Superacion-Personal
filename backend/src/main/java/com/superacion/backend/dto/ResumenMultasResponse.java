package com.superacion.backend.dto;

import com.superacion.backend.model.EstadoMulta;

import java.util.List;

public record ResumenMultasResponse(
        int totalPendiente,
        int totalPagado,
        long cantidadPendientes,
        List<MultaResponse> multas
) {

    public static ResumenMultasResponse de(List<MultaResponse> multas) {
        int pendiente = sumar(multas, EstadoMulta.PENDIENTE);
        int pagado = sumar(multas, EstadoMulta.PAGADA);
        long cantidadPendientes = multas.stream().filter(m -> m.estado() == EstadoMulta.PENDIENTE).count();
        return new ResumenMultasResponse(pendiente, pagado, cantidadPendientes, multas);
    }

    private static int sumar(List<MultaResponse> multas, EstadoMulta estado) {
        return multas.stream()
                .filter(m -> m.estado() == estado)
                .mapToInt(MultaResponse::monto)
                .sum();
    }
}