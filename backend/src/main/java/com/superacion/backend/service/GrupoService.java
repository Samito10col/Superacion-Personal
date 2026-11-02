package com.superacion.backend.service;

import com.superacion.backend.dto.*;
import com.superacion.backend.exception.AccesoDenegadoException;
import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.exception.ReglaNegocioException;
import com.superacion.backend.model.Grupo;
import com.superacion.backend.model.MiembroGrupo;
import com.superacion.backend.model.RolGrupo;
import com.superacion.backend.model.Usuario;
import com.superacion.backend.repository.GrupoRepository;
import com.superacion.backend.repository.MiembroGrupoRepository;
import com.superacion.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final MiembroGrupoRepository miembroGrupoRepository;
    private final UsuarioRepository usuarioRepository;

    public GrupoService(GrupoRepository grupoRepository,
                        MiembroGrupoRepository miembroGrupoRepository,
                        UsuarioRepository usuarioRepository) {
        this.grupoRepository = grupoRepository;
        this.miembroGrupoRepository = miembroGrupoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public GrupoResponse crear(Long usuarioId, CrearGrupoRequest request) {
        Usuario creador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String descripcion = request.descripcion() == null || request.descripcion().isBlank()
                ? null : request.descripcion().trim();

        Grupo grupo = grupoRepository.save(new Grupo(request.nombre().trim(), descripcion, creador));

        // RN-01: quien crea el grupo queda como administrador
        MiembroGrupo admin = miembroGrupoRepository.save(new MiembroGrupo(grupo, creador, RolGrupo.ADMIN));

        return GrupoResponse.desde(admin);
    }

    @Transactional(readOnly = true)
    public List<GrupoResponse> misGrupos(Long usuarioId) {
        return miembroGrupoRepository.findByUsuarioIdAndActivoTrue(usuarioId).stream()
                .map(GrupoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public GrupoDetalleResponse detalle(Long grupoId, Long usuarioId) {
        MiembroGrupo miMembresia = exigirMiembro(grupoId, usuarioId);

        List<MiembroResponse> miembros = miembroGrupoRepository
                .findByGrupoIdAndActivoTrueOrderByUnidoEnAsc(grupoId).stream()
                .map(MiembroResponse::desde)
                .toList();

        return GrupoDetalleResponse.desde(miMembresia, miembros);
    }

    @Transactional
    public MiembroResponse agregarMiembro(Long grupoId, Long usuarioId, AgregarMiembroRequest request) {
        MiembroGrupo miMembresia = exigirMiembro(grupoId, usuarioId);

        // RN-02: solo un admin puede agregar miembros
        if (!miMembresia.esAdmin()) {
            throw new AccesoDenegadoException("Solo un administrador puede agregar miembros");
        }

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Usuario nuevo = usuarioRepository.findByEmail(email)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay un usuario registrado con el email " + email));

        Optional<MiembroGrupo> existente = miembroGrupoRepository.findByGrupoIdAndUsuarioId(grupoId, nuevo.getId());

        MiembroGrupo miembro;
        if (existente.isPresent()) {
            miembro = existente.get();
            if (miembro.isActivo()) {
                throw new ReglaNegocioException(email + " ya es miembro de este grupo");
            }
            miembro.reactivar(); // había salido y vuelve a entrar
        } else {
            miembro = miembroGrupoRepository.save(new MiembroGrupo(miMembresia.getGrupo(), nuevo, RolGrupo.MIEMBRO));
        }

        return MiembroResponse.desde(miembro);
    }

    // Si el usuario no pertenece al grupo, respondemos "no encontrado" para no revelar que el grupo existe
    private MiembroGrupo exigirMiembro(Long grupoId, Long usuarioId) {
        return miembroGrupoRepository.findByGrupoIdAndUsuarioIdAndActivoTrue(grupoId, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado"));
    }
}