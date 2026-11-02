package com.superacion.backend.service;

import com.superacion.backend.dto.RegistroUsuarioRequest;
import com.superacion.backend.dto.UsuarioResponse;
import com.superacion.backend.exception.EmailYaRegistradoException;
import com.superacion.backend.model.Usuario;
import com.superacion.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroUsuarioRequest request) {
        // El email se guarda siempre en minúsculas para evitar duplicados como Ana@x.com y ana@x.com
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        Usuario usuario = new Usuario(request.nombre().trim(), email, passwordHash);

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }
}