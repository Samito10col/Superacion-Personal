package com.superacion.backend.model;

public enum ModoVerificacion {
    AUTODECLARADO,
    EVIDENCIA,
    VALIDACION_PAR,
    EVIDENCIA_Y_VALIDACION;

    public boolean requiereEvidencia() {
        return this == EVIDENCIA || this == EVIDENCIA_Y_VALIDACION;
    }

    public boolean requiereValidacion() {
        return this == VALIDACION_PAR || this == EVIDENCIA_Y_VALIDACION;
    }
}