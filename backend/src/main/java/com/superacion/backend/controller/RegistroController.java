package com.superacion.backend.controller;

import com.superacion.backend.dto.RegistroResponse;
import com.superacion.backend.service.RegistroService;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    // Se envía como formulario (multipart) porque puede llevar una foto
    @PostMapping(value = "/habitos/{habitoId}/registros", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse registrar(@AuthenticationPrincipal Jwt jwt,
                                      @PathVariable Long habitoId,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                      @RequestParam(required = false) String nota,
                                      @RequestPart(name = "evidencia", required = false) MultipartFile evidencia) {
        return registroService.registrar(habitoId, usuarioId(jwt), fecha, nota, evidencia);
    }

    @GetMapping("/habitos/{habitoId}/registros")
    public List<RegistroResponse> listarPeriodoActual(@AuthenticationPrincipal Jwt jwt,
                                                      @PathVariable Long habitoId) {
        return registroService.listarPeriodoActual(habitoId, usuarioId(jwt));
    }

    @GetMapping("/registros/{registroId}/evidencia")
    public ResponseEntity<Resource> evidencia(@AuthenticationPrincipal Jwt jwt,
                                              @PathVariable Long registroId) {
        Resource foto = registroService.evidencia(registroId, usuarioId(jwt));
        MediaType tipo = MediaTypeFactory.getMediaType(foto).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(tipo).body(foto);
    }

    private Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}