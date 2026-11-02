package com.superacion.backend.dto;

import jakarta.validation.constraints.NotNull;

public record AsignarHabitoRequest(

        @NotNull(message = "El id del usuario es obligatorio")
        Long usuarioId
) {
}
