package com.superacion.backend.controller;

import com.superacion.backend.dto.*;
import com.superacion.backend.service.GrupoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GrupoResponse crear(@AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody CrearGrupoRequest request) {
        return grupoService.crear(usuarioId(jwt), request);
    }

    @GetMapping
    public List<GrupoResponse> misGrupos(@AuthenticationPrincipal Jwt jwt) {
        return grupoService.misGrupos(usuarioId(jwt));
    }

    @GetMapping("/{grupoId}")
    public GrupoDetalleResponse detalle(@AuthenticationPrincipal Jwt jwt, @PathVariable Long grupoId) {
        return grupoService.detalle(grupoId, usuarioId(jwt));
    }

    @PostMapping("/{grupoId}/miembros")
    @ResponseStatus(HttpStatus.CREATED)
    public MiembroResponse agregarMiembro(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable Long grupoId,
                                          @Valid @RequestBody AgregarMiembroRequest request) {
        return grupoService.agregarMiembro(grupoId, usuarioId(jwt), request);
    }

    private Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
