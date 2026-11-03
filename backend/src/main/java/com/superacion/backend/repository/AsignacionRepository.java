package com.superacion.backend.repository;

import com.superacion.backend.model.Asignacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // Personas que deben cumplir un hábito
    @EntityGraph(attributePaths = "usuario")
    List<Asignacion> findByHabitoIdAndActivaTrue(Long habitoId);

    // Hábitos que debe cumplir una persona (en todos sus grupos)
    @EntityGraph(attributePaths = {"habito", "habito.grupo"})
    List<Asignacion> findByUsuarioIdAndActivaTrue(Long usuarioId);

    Optional<Asignacion> findByHabitoIdAndUsuarioId(Long habitoId, Long usuarioId);

    // Todas las asignaciones que el cierre de periodos debe revisar
    @EntityGraph(attributePaths = "habito")
    List<Asignacion> findByActivaTrueAndHabitoActivoTrue();
}
