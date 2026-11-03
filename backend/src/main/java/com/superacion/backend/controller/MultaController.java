package com.superacion.backend.controller;

import com.superacion.backend.dto.AnularMultaRequest;
import com.superacion.backend.dto.MultaResponse;
import com.superacion.backend.dto.ResumenMultasResponse;
import com.superacion.backend.service.MultaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MultaController {

    private final MultaService multaService;

    public MultaController(MultaService multaService) {
        this.multaService = multaService;
    }

    @GetMapping("/multas/mias")
    public ResumenMultasResponse misMultas(@AuthenticationPrincipal Jwt jwt) {
        return multaService.misMultas(usuarioId(jwt));
    }

    @GetMapping("/grupos/{grupoId}/multas")
    public ResumenMultasResponse delGrupo(@AuthenticationPrincipal Jwt jwt, @PathVariable Long grupoId) {
        return multaService.delGrupo(grupoId, usuarioId(jwt));
    }

    @PostMapping("/multas/{multaId}/anular")
    public MultaResponse anular(@AuthenticationPrincipal Jwt jwt,
                                @PathVariable Long multaId,
                                @Valid @RequestBody AnularMultaRequest request) {
        return multaService.anular(multaId, usuarioId(jwt), request.motivo());
    }

    private Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}