package com.superacion.backend.controller;

import com.superacion.backend.dto.FondoResponse;
import com.superacion.backend.dto.PagoResponse;
import com.superacion.backend.dto.RechazarRegistroRequest;
import com.superacion.backend.dto.RegistrarPagoRequest;
import com.superacion.backend.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/multas/{multaId}/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    public PagoResponse registrar(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable Long multaId,
                                  @Valid @RequestBody RegistrarPagoRequest request) {
        return pagoService.registrar(multaId, usuarioId(jwt), request);
    }

    @PostMapping("/pagos/{pagoId}/confirmar")
    public PagoResponse confirmar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long pagoId) {
        return pagoService.confirmar(pagoId, usuarioId(jwt));
    }

    // Reutilizamos RechazarRegistroRequest: también pide solo un motivo obligatorio
    @PostMapping("/pagos/{pagoId}/rechazar")
    public PagoResponse rechazar(@AuthenticationPrincipal Jwt jwt,
                                 @PathVariable Long pagoId,
                                 @Valid @RequestBody RechazarRegistroRequest request) {
        return pagoService.rechazar(pagoId, usuarioId(jwt), request.motivo());
    }

    @GetMapping("/grupos/{grupoId}/pagos")
    public List<PagoResponse> delGrupo(@AuthenticationPrincipal Jwt jwt, @PathVariable Long grupoId) {
        return pagoService.delGrupo(grupoId, usuarioId(jwt));
    }

    @GetMapping("/grupos/{grupoId}/fondo")
    public FondoResponse fondo(@AuthenticationPrincipal Jwt jwt, @PathVariable Long grupoId) {
        return pagoService.fondo(grupoId, usuarioId(jwt));
    }

    private Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}