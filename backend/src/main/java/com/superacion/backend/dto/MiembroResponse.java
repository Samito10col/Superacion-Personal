package com.superacion.backend.dto;

import com.superacion.backend.model.MiembroGrupo;
import com.superacion.backend.model.RolGrupo;

import java.time.Instant;

public record MiembroResponse(Long usuarioId, String nombre, String email, RolGrupo rol, Instant unidoEn) {

    public static MiembroResponse desde(MiembroGrupo miembro) {
        return new MiembroResponse(
                miembro.getUsuario().getId(),
                miembro.getUsuario().getNombre(),
                miembro.getUsuario().getEmail(),
                miembro.getRol(),
                miembro.getUnidoEn()
        );
    }
}