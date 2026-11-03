package com.superacion.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResolverApelacionRequest(

        @NotNull(message = "Indica si apruebas (true) o rechazas (false) la apelación")
        Boolean aprobar,

        // Obligatorio solo si se rechaza
        @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
        String motivo
) {
}