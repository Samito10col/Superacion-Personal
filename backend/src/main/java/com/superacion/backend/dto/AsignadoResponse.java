package com.superacion.backend.dto;

import com.superacion.backend.model.Asignacion;

import java.time.LocalDate;

public record AsignadoResponse(Long usuarioId, String nombre, LocalDate vigenteDesde) {

    public static AsignadoResponse desde(Asignacion asignacion) {
        return new AsignadoResponse(
                asignacion.getUsuario().getId(),
                asignacion.getUsuario().getNombre(),
                asignacion.getVigenteDesde()
        );
    }
}