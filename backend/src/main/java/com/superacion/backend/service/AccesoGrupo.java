package com.superacion.backend.service;

import com.superacion.backend.exception.AccesoDenegadoException;
import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.model.MiembroGrupo;
import com.superacion.backend.repository.MiembroGrupoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccesoGrupo {

    private final MiembroGrupoRepository miembroGrupoRepository;

    public AccesoGrupo(MiembroGrupoRepository miembroGrupoRepository) {
        this.miembroGrupoRepository = miembroGrupoRepository;
    }

    // El usuario debe ser miembro activo del grupo; si no, el grupo "no existe" para él
    public MiembroGrupo exigirMiembro(Long grupoId, Long usuarioId) {
        return buscarMiembro(grupoId, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado"));
    }

    // RN-02: el usuario debe ser administrador del grupo
    public MiembroGrupo exigirAdmin(Long grupoId, Long usuarioId) {
        MiembroGrupo miembro = exigirMiembro(grupoId, usuarioId);
        if (!miembro.esAdmin()) {
            throw new AccesoDenegadoException("Solo un administrador del grupo puede hacer esto");
        }
        return miembro;
    }

    public Optional<MiembroGrupo> buscarMiembro(Long grupoId, Long usuarioId) {
        return miembroGrupoRepository.findByGrupoIdAndUsuarioIdAndActivoTrue(grupoId, usuarioId);
    }
}