package com.superacion.backend.service;

import com.superacion.backend.exception.RecursoNoEncontradoException;
import com.superacion.backend.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class AlmacenamientoLocal implements AlmacenamientoEvidencias {

    // Solo se aceptan imágenes de estos tipos
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final Path directorio;

    public AlmacenamientoLocal(@Value("${app.evidencias.directorio}") String directorio) {
        this.directorio = Paths.get(directorio).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.directorio);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de evidencias", e);
        }
    }

    @Override
    public String guardar(MultipartFile archivo) {
        String extension = EXTENSIONES.get(archivo.getContentType());
        if (extension == null) {
            throw new ReglaNegocioException("La evidencia debe ser una imagen JPG, PNG o WEBP");
        }

        // Nombre aleatorio: evita choques y que alguien adivine los nombres de otras fotos
        String nombre = UUID.randomUUID() + "." + extension;

        try (InputStream entrada = archivo.getInputStream()) {
            Files.copy(entrada, directorio.resolve(nombre));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la evidencia", e);
        }
        return nombre;
    }

    @Override
    public Resource cargar(String nombre) {
        Path ruta = directorio.resolve(nombre).normalize();
        // Seguridad: la ruta final debe quedar dentro de la carpeta de evidencias
        if (!ruta.startsWith(directorio) || !Files.exists(ruta)) {
            throw new RecursoNoEncontradoException("Evidencia no encontrada");
        }
        return new FileSystemResource(ruta);
    }
}