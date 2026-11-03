package com.superacion.backend.dto;

import com.superacion.backend.model.Asignacion;
import com.superacion.backend.model.EstadoMulta;
import com.superacion.backend.model.Habito;
import com.superacion.backend.model.Multa;

import java.time.Instant;
import java.time.LocalDate;

public record MultaResponse(
        Long id,
        Long grupoId,
        String grupoNombre,
        Long habitoId,
        String habitoNombre,
        Long usuarioId,
        String usuarioNombre,
        LocalDate periodoInicio,
        LocalDate periodoFin,
        int meta,
        int cumplidos,
        int faltantes,
        int monto,
        EstadoMulta estado,
        String motivoAnulacion,
        Instant creadaEn
) {

    public static MultaResponse desde(Multa multa) {
        Asignacion asignacion = multa.getAsignacion();
        Habito habito = asignacion.getHabito();
        return new MultaResponse(
                multa.getId(),
                habito.getGrupo().getId(),
                habito.getGrupo().getNombre(),
                habito.getId(),
                habito.getNombre(),
                asignacion.getUsuario().getId(),
                asignacion.getUsuario().getNombre(),
                multa.getPeriodoInicio(),
                multa.getPeriodoFin(),
                multa.getMeta(),
                multa.getCumplidos(),
                multa.getFaltantes(),
                multa.getMonto(),
                multa.getEstado(),
                multa.getMotivoAnulacion(),
                multa.getCreadaEn()
        );
    }
}