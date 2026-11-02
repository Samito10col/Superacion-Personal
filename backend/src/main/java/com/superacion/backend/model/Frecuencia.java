package com.superacion.backend.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public enum Frecuencia {
    DIARIA,
    SEMANAL,
    MENSUAL;

    // Primer día del periodo al que pertenece la fecha (RN-05: la semana empieza el lunes)
    public LocalDate inicioDelPeriodo(LocalDate fecha) {
        return switch (this) {
            case DIARIA -> fecha;
            case SEMANAL -> fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MENSUAL -> fecha.withDayOfMonth(1);
        };
    }

    // Último día del periodo al que pertenece la fecha
    public LocalDate finDelPeriodo(LocalDate fecha) {
        return switch (this) {
            case DIARIA -> fecha;
            case SEMANAL -> fecha.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            case MENSUAL -> fecha.with(TemporalAdjusters.lastDayOfMonth());
        };
    }

    // Primer día del periodo siguiente (RN-07: las obligaciones empiezan en un periodo completo)
    public LocalDate inicioDelSiguientePeriodo(LocalDate fecha) {
        return finDelPeriodo(fecha).plusDays(1);
    }

    // Máximo de veces que se puede cumplir en un periodo (RN-09: máximo un registro por día)
    public int metaMaxima() {
        return switch (this) {
            case DIARIA -> 1;
            case SEMANAL -> 7;
            case MENSUAL -> 28;
        };
    }
}