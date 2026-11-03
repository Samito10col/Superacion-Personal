package com.superacion.backend.service;

import com.superacion.backend.config.ZonaHoraria;
import com.superacion.backend.model.*;
import com.superacion.backend.repository.AsignacionRepository;
import com.superacion.backend.repository.MultaRepository;
import com.superacion.backend.repository.RegistroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CierrePeriodos {

    private static final Logger log = LoggerFactory.getLogger(CierrePeriodos.class);

    private final AsignacionRepository asignacionRepository;
    private final RegistroRepository registroRepository;
    private final MultaRepository multaRepository;
    private final AprobacionAutomatica aprobacionAutomatica;
    private final long horasValidacion;

    public CierrePeriodos(AsignacionRepository asignacionRepository,
                          RegistroRepository registroRepository,
                          MultaRepository multaRepository,
                          AprobacionAutomatica aprobacionAutomatica,
                          @Value("${app.validacion.horas-limite}") long horasValidacion) {
        this.asignacionRepository = asignacionRepository;
        this.registroRepository = registroRepository;
        this.multaRepository = multaRepository;
        this.aprobacionAutomatica = aprobacionAutomatica;
        this.horasValidacion = horasValidacion;
    }

    // Corre 1 minuto después de arrancar y luego cada hora
    @Scheduled(initialDelayString = "PT1M", fixedDelayString = "PT1H")
    @Transactional
    public void cerrarPeriodosVencidos() {
        // Primero aprobamos los pendientes vencidos (RN-12), para que cuenten en el cierre
        aprobacionAutomatica.aprobarPendientesVencidos();

        Instant ahora = Instant.now();
        int multasGeneradas = 0;

        for (Asignacion asignacion : asignacionRepository.findByActivaTrueAndHabitoActivoTrue()) {
            multasGeneradas += evaluar(asignacion, ahora);
        }

        if (multasGeneradas > 0) {
            log.info("Cierre de periodos: {} multa(s) generada(s)", multasGeneradas);
        }
    }

    private int evaluar(Asignacion asignacion, Instant ahora) {
        Habito habito = asignacion.getHabito();
        Frecuencia frecuencia = habito.getFrecuencia();

        // Empieza en el primer periodo que aún no se ha evaluado
        LocalDate inicio = asignacion.getVigenteDesde();
        if (asignacion.getEvaluadaHasta() != null && asignacion.getEvaluadaHasta().isAfter(inicio.minusDays(1))) {
            inicio = asignacion.getEvaluadaHasta().plusDays(1);
        }
        // Si cae a mitad de un periodo, se pasa al siguiente periodo completo (RN-07)
        if (!inicio.equals(frecuencia.inicioDelPeriodo(inicio))) {
            inicio = frecuencia.inicioDelSiguientePeriodo(inicio);
        }

        int generadas = 0;
        while (true) {
            LocalDate fin = frecuencia.finDelPeriodo(inicio);

            // RN-14: el periodo se cierra 48 h después de terminar (hora de Colombia)
            Instant momentoDeCierre = fin.plusDays(1)
                    .atStartOfDay(ZonaHoraria.COLOMBIA)
                    .toInstant()
                    .plus(horasValidacion, ChronoUnit.HOURS);
            if (ahora.isBefore(momentoDeCierre)) {
                break;
            }

            // Si hay apelaciones sin resolver, esperamos a que el admin decida
            if (registroRepository.existsByAsignacionIdAndFechaBetweenAndEstado(
                    asignacion.getId(), inicio, fin, EstadoRegistro.APELADO)) {
                break;
            }

            int cumplidos = (int) registroRepository.countByAsignacionIdAndFechaBetweenAndEstado(
                    asignacion.getId(), inicio, fin, EstadoRegistro.APROBADO);
            int monto = calcularMonto(habito, cumplidos);

            if (monto > 0 && !multaRepository.existsByAsignacionIdAndPeriodoInicio(asignacion.getId(), inicio)) {
                multaRepository.save(new Multa(asignacion, inicio, fin,
                        habito.getMetaPorPeriodo(), cumplidos, monto));
                generadas++;
            }

            asignacion.marcarEvaluadaHasta(fin);
            inicio = fin.plusDays(1);
        }
        return generadas;
    }

    // RN-15: FIJA paga el valor completo; POR_FALTA paga por cada vez que faltó
    static int calcularMonto(Habito habito, int cumplidos) {
        int faltantes = Math.max(0, habito.getMetaPorPeriodo() - cumplidos);
        if (faltantes == 0) {
            return 0;
        }
        return habito.getTipoMulta() == TipoMulta.FIJA
                ? habito.getValorMulta()
                : habito.getValorMulta() * faltantes;
    }
}