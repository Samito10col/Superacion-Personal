package com.superacion.backend.dto;

import com.superacion.backend.model.Asignacion;
import com.superacion.backend.model.EstadoRegistro;
import com.superacion.backend.model.Registro;

import java.time.Instant;
import java.time.LocalDate;

public record RegistroResponse(
        Long id,
        Long habitoId,
        Long usuarioId,
        String usuarioNombre,
        LocalDate fecha,
        EstadoRegistro estado,
        String nota,
        boolean tieneEvidencia,
        String motivoRechazo,
        Instant creadoEn
) {

    public static RegistroResponse desde(Registro registro) {
        Asignacion asignacion = registro.getAsignacion();
        return new RegistroResponse(
                registro.getId(),
                asignacion.getHabito().getId(),
                asignacion.getUsuario().getId(),
                asignacion.getUsuario().getNombre(),
                registro.getFecha(),
                registro.getEstado(),
                registro.getNota(),
                registro.tieneEvidencia(),
                registro.getMotivoRechazo(),
                registro.getCreadoEn()
        );
    }
}