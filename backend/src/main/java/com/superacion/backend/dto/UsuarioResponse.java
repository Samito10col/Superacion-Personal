package com.superacion.backend.dto;

import com.superacion.backend.model.Usuario;

import java.time.Instant;

public record UsuarioResponse(Long id, String nombre, String email, Instant creadoEn) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getCreadoEn()
        );
    }
}