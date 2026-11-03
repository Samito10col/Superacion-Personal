package com.superacion.backend.dto;

import java.util.List;

public record FondoResponse(
        Long grupoId,
        long totalRecaudado,       // RN-22: suma de pagos confirmados
        long saldoPorCobrar,       // lo que todavía deben en multas pendientes
        long pagosPorConfirmar,    // pagos que el admin aún no revisa
        List<PagoResponse> movimientos
) {
}