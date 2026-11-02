package com.superacion.backend.config;

import java.time.LocalDate;
import java.time.ZoneId;

public final class ZonaHoraria {

    public static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");

    private ZonaHoraria() {
    }

    public static LocalDate hoy() {
        return LocalDate.now(COLOMBIA);
    }
}