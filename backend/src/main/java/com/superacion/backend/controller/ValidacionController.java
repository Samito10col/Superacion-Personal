package com.superacion.backend.controller;

import com.superacion.backend.dto.RechazarRegistroRequest;
import com.superacion.backend.dto.RegistroResponse;
import com.superacion.backend.dto.ResolverApelacionRequest;
import com.superacion.backend.service.ValidacionService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registros")
public class ValidacionController {

    private final ValidacionService validacionService;

    public ValidacionController(ValidacionService validacionService) {
        this.validacionService = validacionService;
    }

    @GetMapping("/pendientes")
    public List<RegistroResponse> pendientes(@AuthenticationPrincipal Jwt jwt) {
        return validacionService.pendientes(usuarioId(jwt));
    }

    @PostMapping("/{registroId}/aprobar")
    public RegistroResponse aprobar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long registroId) {
        return validacionService.aprobar(registroId, usuarioId(jwt));
    }

    @PostMapping("/{registroId}/rechazar")
    public RegistroResponse rechazar(@AuthenticationPrincipal Jwt jwt,
                                     @PathVariable Long registroId,
                                     @Valid @RequestBody RechazarRegistroRequest request) {
        return validacionService.rechazar(registroId, usuarioId(jwt), request.motivo());
    }

    @PostMapping("/{registroId}/apelar")
    public RegistroResponse apelar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long registroId) {
        return validacionService.apelar(registroId, usuarioId(jwt));
    }

    @PostMapping("/{registroId}/resolver-apelacion")
    public RegistroResponse resolverApelacion(@AuthenticationPrincipal Jwt jwt,
                                              @PathVariable Long registroId,
                                              @Valid @RequestBody ResolverApelacionRequest request) {
        return validacionService.resolverApelacion(registroId, usuarioId(jwt), request);
    }

    private Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}