package com.superacion.backend.dto;

import com.superacion.backend.model.Frecuencia;
import com.superacion.backend.model.ModoVerificacion;
import com.superacion.backend.model.TipoMulta;
import jakarta.validation.constraints.*;

public record CrearHabitoRequest(

        @NotBlank(message = "El nombre del hábito es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        String descripcion,

        @NotNull(message = "La frecuencia es obligatoria (DIARIA, SEMANAL o MENSUAL)")
        Frecuencia frecuencia,

        @NotNull(message = "La meta por periodo es obligatoria")
        @Min(value = 1, message = "La meta debe ser al menos 1")
        Integer metaPorPeriodo,

        @NotNull(message = "El tipo de multa es obligatorio (FIJA o POR_FALTA)")
        TipoMulta tipoMulta,

        @NotNull(message = "El valor de la multa es obligatorio")
        @Positive(message = "El valor de la multa debe ser mayor que cero")
        @Max(value = 10_000_000, message = "El valor de la multa no puede superar 10.000.000")
        Integer valorMulta,

        @NotNull(message = "El modo de verificación es obligatorio")
        ModoVerificacion modoVerificacion,

        // Si no se envía, el hábito se asigna a todos los miembros del grupo
        Boolean asignarATodos
) {
}