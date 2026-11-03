package com.superacion.backend.repository;

import com.superacion.backend.model.EstadoRegistro;
import com.superacion.backend.model.Registro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    // RN-09: ¿ya registró este hábito ese día?
    boolean existsByAsignacionIdAndFecha(Long asignacionId, LocalDate fecha);

    // Registros que cuentan en un periodo (todos menos los rechazados)
    long countByAsignacionIdAndFechaBetweenAndEstadoNot(Long asignacionId, LocalDate desde,
                                                        LocalDate hasta, EstadoRegistro estado);

    // Cierre de periodos: cuántos registros quedaron en un estado dado
    long countByAsignacionIdAndFechaBetweenAndEstado(Long asignacionId, LocalDate desde,
                                                     LocalDate hasta, EstadoRegistro estado);

    // Cierre de periodos: ¿hay registros en ese estado (por ejemplo, apelaciones sin resolver)?
    boolean existsByAsignacionIdAndFechaBetweenAndEstado(Long asignacionId, LocalDate desde,
                                                         LocalDate hasta, EstadoRegistro estado);

    // Registros de todas las personas de un hábito en un rango de fechas
    @EntityGraph(attributePaths = {"asignacion", "asignacion.usuario"})
    List<Registro> findByAsignacionHabitoIdAndFechaBetweenOrderByFechaDescCreadoEnDesc(
            Long habitoId, LocalDate desde, LocalDate hasta);

    // Registros de compañeros (de mis grupos) que esperan validación, sin incluir los míos
    @Query("""
            select r from Registro r
            join fetch r.asignacion a
            join fetch a.usuario u
            join fetch a.habito h
            where r.estado = :estado
              and u.id <> :usuarioId
              and exists (
                  select 1 from MiembroGrupo m
                  where m.grupo = h.grupo and m.usuario.id = :usuarioId and m.activo = true
              )
            order by r.creadoEn asc
            """)
    List<Registro> pendientesParaValidar(@Param("usuarioId") Long usuarioId,
                                         @Param("estado") EstadoRegistro estado);

    // RN-12: aprueba de una sola vez todos los pendientes más antiguos que el límite
    @Modifying(clearAutomatically = true)
    @Query("""
            update Registro r
            set r.estado = :aprobado, r.fechaValidacion = :ahora
            where r.estado = :pendiente and r.creadoEn < :limite
            """)
    int aprobarPendientesVencidos(@Param("aprobado") EstadoRegistro aprobado,
                                  @Param("pendiente") EstadoRegistro pendiente,
                                  @Param("ahora") Instant ahora,
                                  @Param("limite") Instant limite);
}