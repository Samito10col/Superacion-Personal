package com.superacion.backend.controller;

import com.superacion.backend.dto.RegistroUsuarioRequest;
import com.superacion.backend.dto.UsuarioResponse;
import com.superacion.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        return usuarioService.registrar(request);
    }

    @GetMapping("/me")
    public UsuarioResponse miPerfil(@AuthenticationPrincipal Jwt jwt) {
        // El "subject" del token es el id del usuario que inició sesión
        return usuarioService.obtenerPorId(Long.valueOf(jwt.getSubject()));
    }
}