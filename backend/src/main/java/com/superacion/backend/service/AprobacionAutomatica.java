package com.superacion.backend.service;

import com.superacion.backend.model.EstadoRegistro;
import com.superacion.backend.repository.RegistroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class AprobacionAutomatica {

    private static final Logger log = LoggerFactory.getLogger(AprobacionAutomatica.class);

    private final RegistroRepository registroRepository;
    private final long horasLimite;

    public AprobacionAutomatica(RegistroRepository registroRepository,
                                @Value("${app.validacion.horas-limite}") long horasLimite) {
        this.registroRepository = registroRepository;
        this.horasLimite = horasLimite;
    }

    // Corre 30 segundos después de arrancar y luego cada 15 minutos
    @Scheduled(initialDelayString = "PT30S", fixedDelayString = "PT15M")
    @Transactional
    public void aprobarPendientesVencidos() {
        Instant ahora = Instant.now();
        Instant limite = ahora.minus(horasLimite, ChronoUnit.HOURS);

        int aprobados = registroRepository.aprobarPendientesVencidos(
                EstadoRegistro.APROBADO, EstadoRegistro.PENDIENTE_VALIDACION, ahora, limite);

        if (aprobados > 0) {
            log.info("RN-12: {} registro(s) aprobados automáticamente por falta de validación", aprobados);
        }
    }
}