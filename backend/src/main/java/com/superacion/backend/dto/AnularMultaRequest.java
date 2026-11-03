package com.superacion.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnularMultaRequest(

        // RN-17 y RN-19: toda anulación debe quedar justificada
        @NotBlank(message = "Debes escribir el motivo de la anulación")
        @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
        String motivo
) {
}