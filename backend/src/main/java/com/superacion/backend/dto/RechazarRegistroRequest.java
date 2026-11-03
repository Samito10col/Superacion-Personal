package com.superacion.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RechazarRegistroRequest(

        // RN-11: el rechazo exige un motivo
        @NotBlank(message = "Debes escribir el motivo del rechazo")
        @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
        String motivo
) {
}