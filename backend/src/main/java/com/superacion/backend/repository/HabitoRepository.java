package com.superacion.backend.repository;

import com.superacion.backend.model.Habito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitoRepository extends JpaRepository<Habito, Long> {

    List<Habito> findByGrupoIdAndActivoTrueOrderByCreadoEnAsc(Long grupoId);
}