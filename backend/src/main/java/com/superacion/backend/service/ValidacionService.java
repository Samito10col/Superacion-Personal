package com.superacion.backend.service;

import com.superacion.backend.dto.RegistroResponse;
import com.superacion.backend.dto.ResolverApelacionRequest;
import com.superacion.backend.exception.AccesoDenegadoException;
import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.exception.ReglaNegocioException;
import com.superacion.backend.model.EstadoRegistro;
import com.superacion.backend.model.Registro;
import com.superacion.backend.model.Usuario;
import com.superacion.backend.repository.RegistroRepository;
import com.superacion.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ValidacionService {

    private final RegistroRepository registroRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccesoGrupo accesoGrupo;

    public ValidacionService(RegistroRepository registroRepository,
                             UsuarioRepository usuarioRepository,
                             AccesoGrupo accesoGrupo) {
        this.registroRepository = registroRepository;
        this.usuarioRepository = usuarioRepository;
        this.accesoGrupo = accesoGrupo;
    }

    @Transactional(readOnly = true)
    public List<RegistroResponse> pendientes(Long usuarioId) {
        return registroRepository.pendientesParaValidar(usuarioId, EstadoRegistro.PENDIENTE_VALIDACION)
                .stream()
                .map(RegistroResponse::desde)
                .toList();
    }

    @Transactional
    public RegistroResponse aprobar(Long registroId, Long usuarioId) {
        Registro registro = buscar(registroId);
        exigirQuePuedaValidar(registro, usuarioId);
        registro.aprobar(usuario(usuarioId));
        return RegistroResponse.desde(registro);
    }

    @Transactional
    public RegistroResponse rechazar(Long registroId, Long usuarioId, String motivo) {
        Registro registro = buscar(registroId);
        exigirQuePuedaValidar(registro, usuarioId);
        registro.rechazar(usuario(usuarioId), motivo.trim());
        return RegistroResponse.desde(registro);
    }

    // RN-13: el dueño puede apelar un rechazo, una sola vez
    @Transactional
    public RegistroResponse apelar(Long registroId, Long usuarioId) {
        Registro registro = buscar(registroId);
        accesoGrupo.exigirMiembro(registro.getGrupoId(), usuarioId);

        if (!registro.perteneceA(usuarioId)) {
            throw new AccesoDenegadoException("Solo puedes apelar tus propios registros");
        }
        if (registro.getEstado() != EstadoRegistro.RECHAZADO) {
            throw new ReglaNegocioException("Solo se pueden apelar registros rechazados");
        }
        if (registro.isApelado()) {
            throw new ReglaNegocioException("Ya apelaste este registro; solo se permite una apelación");
        }

        registro.apelar();
        return RegistroResponse.desde(registro);
    }

    // RN-13: un admin decide la apelación de forma definitiva
    @Transactional
    public RegistroResponse resolverApelacion(Long registroId, Long usuarioId, ResolverApelacionRequest request) {
        Registro registro = buscar(registroId);
        accesoGrupo.exigirAdmin(registro.getGrupoId(), usuarioId);

        if (registro.getEstado() != EstadoRegistro.APELADO) {
            throw new ReglaNegocioException("Este registro no tiene una apelación pendiente");
        }
        if (registro.perteneceA(usuarioId)) {
            throw new AccesoDenegadoException("No puedes resolver tu propia apelación; debe hacerlo otro administrador");
        }

        if (request.aprobar()) {
            registro.aprobar(usuario(usuarioId));
        } else {
            if (request.motivo() == null || request.motivo().isBlank()) {
                throw new ReglaNegocioException("Para rechazar la apelación debes escribir un motivo");
            }
            registro.rechazar(usuario(usuarioId), request.motivo().trim());
        }
        return RegistroResponse.desde(registro);
    }

    private void exigirQuePuedaValidar(Registro registro, Long usuarioId) {
        accesoGrupo.exigirMiembro(registro.getGrupoId(), usuarioId);

        // RN-10: nadie valida su propio registro
        if (registro.perteneceA(usuarioId)) {
            throw new AccesoDenegadoException("No puedes validar tu propio registro");
        }
        if (registro.getEstado() != EstadoRegistro.PENDIENTE_VALIDACION) {
            throw new ReglaNegocioException("Este registro no está pendiente de validación (estado actual: "
                    + registro.getEstado() + ")");
        }
    }

    private Registro buscar(Long registroId) {
        return registroRepository.findById(registroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro no encontrado"));
    }

    private Usuario usuario(Long usuarioId) {
        return usuarioRepository.getReferenceById(usuarioId);
    }
}