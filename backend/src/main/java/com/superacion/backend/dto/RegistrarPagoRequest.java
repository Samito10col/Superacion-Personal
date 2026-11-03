package com.superacion.backend.dto;

import com.superacion.backend.model.MetodoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistrarPagoRequest(

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor que cero")
        Integer monto,

        @NotNull(message = "El método es obligatorio (EFECTIVO, TRANSFERENCIA, NEQUI, DAVIPLATA u OTRO)")
        MetodoPago metodo,

        // Número de comprobante o referencia de la transferencia (opcional)
        @Size(max = 100, message = "La referencia no puede superar 100 caracteres")
        String referencia
) {
}