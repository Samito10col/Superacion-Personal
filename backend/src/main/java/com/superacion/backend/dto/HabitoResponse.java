package com.superacion.backend.dto;

import com.superacion.backend.model.Frecuencia;
import com.superacion.backend.model.Habito;
import com.superacion.backend.model.ModoVerificacion;
import com.superacion.backend.model.TipoMulta;

import java.time.Instant;
import java.util.List;

public record HabitoResponse(
        Long id,
        Long grupoId,
        String nombre,
        String descripcion,
        Frecuencia frecuencia,
        int metaPorPeriodo,
        TipoMulta tipoMulta,
        int valorMulta,
        ModoVerificacion modoVerificacion,
        Instant creadoEn,
        List<AsignadoResponse> asignados
) {

    public static HabitoResponse desde(Habito habito, List<AsignadoResponse> asignados) {
        return new HabitoResponse(
                habito.getId(),
                habito.getGrupo().getId(),
                habito.getNombre(),
                habito.getDescripcion(),
                habito.getFrecuencia(),
                habito.getMetaPorPeriodo(),
                habito.getTipoMulta(),
                habito.getValorMulta(),
                habito.getModoVerificacion(),
                habito.getCreadoEn(),
                asignados
        );
    }
}