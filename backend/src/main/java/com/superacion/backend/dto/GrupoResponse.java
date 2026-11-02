package com.superacion.backend.dto;

import com.superacion.backend.model.Grupo;
import com.superacion.backend.model.MiembroGrupo;
import com.superacion.backend.model.RolGrupo;

import java.time.Instant;

public record GrupoResponse(Long id, String nombre, String descripcion, RolGrupo miRol, Instant creadoEn) {

    public static GrupoResponse desde(MiembroGrupo miMembresia) {
        Grupo grupo = miMembresia.getGrupo();
        return new GrupoResponse(
                grupo.getId(),
                grupo.getNombre(),
                grupo.getDescripcion(),
                miMembresia.getRol(),
                grupo.getCreadoEn()
        );
    }
}