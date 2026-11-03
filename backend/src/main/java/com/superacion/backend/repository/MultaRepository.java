package com.superacion.backend.repository;

import com.superacion.backend.model.Multa;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MultaRepository extends JpaRepository<Multa, Long> {

    boolean existsByAsignacionIdAndPeriodoInicio(Long asignacionId, LocalDate periodoInicio);

    // Multas de una persona (en todos sus grupos)
    @EntityGraph(attributePaths = {"asignacion", "asignacion.habito", "asignacion.habito.grupo", "asignacion.usuario"})
    List<Multa> findByAsignacionUsuarioIdOrderByPeriodoInicioDescIdDesc(Long usuarioId);

    // Multas de todo un grupo
    @EntityGraph(attributePaths = {"asignacion", "asignacion.habito", "asignacion.habito.grupo", "asignacion.usuario"})
    List<Multa> findByAsignacionHabitoGrupoIdOrderByPeriodoInicioDescIdDesc(Long grupoId);
}