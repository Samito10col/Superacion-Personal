package com.superacion.backend.repository;

import com.superacion.backend.model.EstadoRegistro;
import com.superacion.backend.model.Registro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    // RN-09: ¿ya registró este hábito ese día?
    boolean existsByAsignacionIdAndFecha(Long asignacionId, LocalDate fecha);

    // Registros que cuentan en un periodo (todos menos los rechazados)
    long countByAsignacionIdAndFechaBetweenAndEstadoNot(Long asignacionId, LocalDate desde,
                                                        LocalDate hasta, EstadoRegistro estado);

    // Registros de todas las personas de un hábito en un rango de fechas
    @EntityGraph(attributePaths = {"asignacion", "asignacion.usuario"})
    List<Registro> findByAsignacionHabitoIdAndFechaBetweenOrderByFechaDescCreadoEnDesc(
            Long habitoId, LocalDate desde, LocalDate hasta);
}