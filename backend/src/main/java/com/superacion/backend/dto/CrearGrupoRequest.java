package com.superacion.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearGrupoRequest(

        @NotBlank(message = "El nombre del grupo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        String descripcion
) {
}