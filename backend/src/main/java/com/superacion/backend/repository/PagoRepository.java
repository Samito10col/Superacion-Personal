package com.superacion.backend.repository;

import com.superacion.backend.model.EstadoMulta;
import com.superacion.backend.model.EstadoPago;
import com.superacion.backend.model.Pago;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Suma de los pagos de una multa que están en ciertos estados
    @Query("select coalesce(sum(p.monto), 0) from Pago p where p.multa.id = :multaId and p.estado in :estados")
    long sumarPorMultaYEstados(@Param("multaId") Long multaId,
                               @Param("estados") Collection<EstadoPago> estados);

    boolean existsByMultaIdAndEstadoIn(Long multaId, Collection<EstadoPago> estados);

    // Historial de pagos de un grupo
    @EntityGraph(attributePaths = {"multa", "multa.asignacion", "multa.asignacion.usuario", "multa.asignacion.habito"})
    List<Pago> findByMultaAsignacionHabitoGrupoIdOrderByCreadoEnDesc(Long grupoId);

    // RN-22: total recaudado (pagos confirmados) en el grupo
    @Query("""
            select coalesce(sum(p.monto), 0) from Pago p
            where p.multa.asignacion.habito.grupo.id = :grupoId and p.estado = :estado
            """)
    long sumarPorGrupoYEstado(@Param("grupoId") Long grupoId, @Param("estado") EstadoPago estado);

    // Lo ya abonado (confirmado) a multas que todavía no están pagadas del todo
    @Query("""
            select coalesce(sum(p.monto), 0) from Pago p
            where p.multa.asignacion.habito.grupo.id = :grupoId
              and p.estado = :estadoPago and p.multa.estado = :estadoMulta
            """)
    long sumarAbonosDeMultasEnEstado(@Param("grupoId") Long grupoId,
                                     @Param("estadoPago") EstadoPago estadoPago,
                                     @Param("estadoMulta") EstadoMulta estadoMulta);

    long countByMultaAsignacionHabitoGrupoIdAndEstado(Long grupoId, EstadoPago estado);
}