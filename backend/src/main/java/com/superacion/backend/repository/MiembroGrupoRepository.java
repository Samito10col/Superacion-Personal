package com.superacion.backend.repository;

import com.superacion.backend.model.MiembroGrupo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MiembroGrupoRepository extends JpaRepository<MiembroGrupo, Long> {

    // Grupos activos de un usuario (trae también los datos del grupo)
    @EntityGraph(attributePaths = "grupo")
    List<MiembroGrupo> findByUsuarioIdAndActivoTrue(Long usuarioId);

    // Miembros activos de un grupo (trae también los datos de cada usuario)
    @EntityGraph(attributePaths = "usuario")
    List<MiembroGrupo> findByGrupoIdAndActivoTrueOrderByUnidoEnAsc(Long grupoId);

    @EntityGraph(attributePaths = "grupo")
    Optional<MiembroGrupo> findByGrupoIdAndUsuarioIdAndActivoTrue(Long grupoId, Long usuarioId);

    Optional<MiembroGrupo> findByGrupoIdAndUsuarioId(Long grupoId, Long usuarioId);
}