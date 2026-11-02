package com.superacion.backend.controller;

import com.superacion.backend.dto.*;
import com.superacion.backend.service.HabitoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HabitoController {

    private final HabitoService habitoService;

    public HabitoController(HabitoService habitoService) {
        this.habitoService = habitoService;
    }

    @PostMapping("/grupos/{grupoId}/habitos")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitoResponse crear(@AuthenticationPrincipal Jwt jwt,
                                @PathVariable Long grupoId,
                                @Valid @RequestBody CrearHabitoRequest request) {
        return habitoService.crear(grupoId, usuarioId(jwt), request);
    }

    @GetMapping("/grupos/{grupoId}/habitos")
    public List<HabitoResponse> listarDelGrupo(@AuthenticationPrincipal Jwt jwt, @PathVariable Long grupoId) {
        return habitoService.listarDelGrupo(grupoId, usuarioId(jwt));
    }

    @PostMapping("/habitos/{habitoId}/asignaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public AsignadoResponse asignar(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable Long habitoId,
                                    @Valid @RequestBody AsignarHabitoRequest request) {
        return habitoService.asignar(habitoId, usuarioId(jwt), request);
    }

    @GetMapping("/habitos/mios")
    public List<MiHabitoResponse> misHabitos(@AuthenticationPrincipal Jwt jwt) {
        return habitoService.misHabitos(usuarioId(jwt));
    }

    private Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}