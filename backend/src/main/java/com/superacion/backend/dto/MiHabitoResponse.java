package com.superacion.backend.dto;

import com.superacion.backend.model.*;

import java.time.LocalDate;

public record MiHabitoResponse(
        Long habitoId,
        String nombre,
        Long grupoId,
        String grupoNombre,
        Frecuencia frecuencia,
        int metaPorPeriodo,
        TipoMulta tipoMulta,
        int valorMulta,
        ModoVerificacion modoVerificacion,
        LocalDate vigenteDesde,
        LocalDate periodoActualInicio,
        LocalDate periodoActualFin,
        int cumplidosEnPeriodo,   // registros del periodo actual que cuentan (sin los rechazados)
        boolean registradoHoy     // ¿ya marcó "lo hice" hoy?
) {

    public static MiHabitoResponse desde(Asignacion asignacion, LocalDate hoy,
                                         int cumplidosEnPeriodo, boolean registradoHoy) {
        Habito habito = asignacion.getHabito();
        Frecuencia frecuencia = habito.getFrecuencia();
        return new MiHabitoResponse(
                habito.getId(),
                habito.getNombre(),
                habito.getGrupo().getId(),
                habito.getGrupo().getNombre(),
                frecuencia,
                habito.getMetaPorPeriodo(),
                habito.getTipoMulta(),
                habito.getValorMulta(),
                habito.getModoVerificacion(),
                asignacion.getVigenteDesde(),
                frecuencia.inicioDelPeriodo(hoy),
                frecuencia.finDelPeriodo(hoy),
                cumplidosEnPeriodo,
                registradoHoy
        );
    }
}