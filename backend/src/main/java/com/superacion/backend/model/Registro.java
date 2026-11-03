package com.superacion.backend.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "registros")
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignacion_id", nullable = false)
    private Asignacion asignacion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoRegistro estado;

    @Column(length = 500)
    private String nota;

    @Column(name = "evidencia_ruta", length = 255)
    private String evidenciaRuta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por")
    private Usuario validadoPor;

    @Column(name = "fecha_validacion")
    private Instant fechaValidacion;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(nullable = false)
    private boolean apelado = false;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected Registro() {
    }

    public Registro(Asignacion asignacion, LocalDate fecha, EstadoRegistro estado,
                    String nota, String evidenciaRuta) {
        this.asignacion = asignacion;
        this.fecha = fecha;
        this.estado = estado;
        this.nota = nota;
        this.evidenciaRuta = evidenciaRuta;
    }

    @PrePersist
    void alCrear() {
        this.creadoEn = Instant.now();
    }

    public void aprobar(Usuario validador) {
        this.estado = EstadoRegistro.APROBADO;
        this.validadoPor = validador;
        this.fechaValidacion = Instant.now();
        this.motivoRechazo = null;
    }

    public void rechazar(Usuario validador, String motivo) {
        this.estado = EstadoRegistro.RECHAZADO;
        this.validadoPor = validador;
        this.fechaValidacion = Instant.now();
        this.motivoRechazo = motivo;
    }

    // RN-13: solo se permite una apelación por registro
    public void apelar() {
        this.estado = EstadoRegistro.APELADO;
        this.apelado = true;
    }

    public boolean tieneEvidencia() {
        return evidenciaRuta != null;
    }

    public boolean perteneceA(Long usuarioId) {
        return asignacion.getUsuario().getId().equals(usuarioId);
    }

    public Long getGrupoId() {
        return asignacion.getHabito().getGrupo().getId();
    }

    public Long getId() { return id; }
    public Asignacion getAsignacion() { return asignacion; }
    public LocalDate getFecha() { return fecha; }
    public EstadoRegistro getEstado() { return estado; }
    public String getNota() { return nota; }
    public String getEvidenciaRuta() { return evidenciaRuta; }
    public Usuario getValidadoPor() { return validadoPor; }
    public Instant getFechaValidacion() { return fechaValidacion; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public boolean isApelado() { return apelado; }
    public Instant getCreadoEn() { return creadoEn; }
}