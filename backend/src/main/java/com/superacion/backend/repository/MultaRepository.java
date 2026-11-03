package com.superacion.backend.repository;

import com.superacion.backend.model.EstadoMulta;
import com.superacion.backend.model.Multa;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Suma de las multas de un grupo en un estado (por ejemplo, todas las pendientes)
    @Query("""
            select coalesce(sum(m.monto), 0) from Multa m
            where m.asignacion.habito.grupo.id = :grupoId and m.estado = :estado
            """)
    long sumarPorGrupoYEstado(@Param("grupoId") Long grupoId, @Param("estado") EstadoMulta estado);
}