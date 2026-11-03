package com.superacion.backend.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habito_id", nullable = false)
    private Habito habito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private boolean activa = true;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    // Último día ya evaluado por el cierre de periodos (null = nunca evaluada)
    @Column(name = "evaluada_hasta")
    private LocalDate evaluadaHasta;

    @Column(name = "asignada_en", nullable = false, updatable = false)
    private Instant asignadaEn;

    protected Asignacion() {
    }

    public Asignacion(Habito habito, Usuario usuario, LocalDate vigenteDesde) {
        this.habito = habito;
        this.usuario = usuario;
        this.vigenteDesde = vigenteDesde;
    }

    @PrePersist
    void alCrear() {
        this.asignadaEn = Instant.now();
    }

    // Vuelve a asignar el hábito a alguien que lo tenía desactivado
    public void reactivar(LocalDate desde) {
        this.activa = true;
        this.vigenteDesde = desde;
    }

    public void desactivar() {
        this.activa = false;
    }

    public void marcarEvaluadaHasta(LocalDate fecha) {
        this.evaluadaHasta = fecha;
    }

    public Long getId() { return id; }
    public Habito getHabito() { return habito; }
    public Usuario getUsuario() { return usuario; }
    public boolean isActiva() { return activa; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public LocalDate getEvaluadaHasta() { return evaluadaHasta; }
    public Instant getAsignadaEn() { return asignadaEn; }
}