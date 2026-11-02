package com.superacion.backend.dto;

import com.superacion.backend.model.Grupo;
import com.superacion.backend.model.MiembroGrupo;
import com.superacion.backend.model.RolGrupo;

import java.time.Instant;
import java.util.List;

public record GrupoDetalleResponse(
        Long id,
        String nombre,
        String descripcion,
        RolGrupo miRol,
        Instant creadoEn,
        List<MiembroResponse> miembros
) {

    public static GrupoDetalleResponse desde(MiembroGrupo miMembresia, List<MiembroResponse> miembros) {
        Grupo grupo = miMembresia.getGrupo();
        return new GrupoDetalleResponse(
                grupo.getId(),
                grupo.getNombre(),
                grupo.getDescripcion(),
                miMembresia.getRol(),
                grupo.getCreadoEn(),
                miembros
        );
    }
}