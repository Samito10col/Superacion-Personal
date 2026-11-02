package com.superacion.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AlmacenamientoEvidencias {

    // Guarda la foto y devuelve el nombre con el que quedó guardada
    String guardar(MultipartFile archivo);

    // Devuelve la foto para poder enviarla a la app
    Resource cargar(String nombre);
}