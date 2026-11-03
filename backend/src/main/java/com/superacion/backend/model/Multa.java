package com.superacion.backend.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "multas")
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignacion_id", nullable = false)
    private Asignacion asignacion;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(nullable = false)
    private int meta;

    @Column(nullable = false)
    private int cumplidos;

    // RN-16: el monto queda congelado al generarse
    @Column(nullable = false)
    private int monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoMulta estado;

    @Column(name = "motivo_anulacion", length = 500)
    private String motivoAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anulada_por")
    private Usuario anuladaPor;

    @Column(name = "anulada_en")
    private Instant anuladaEn;

    @Column(name = "creada_en", nullable = false, updatable = false)
    private Instant creadaEn;

    protected Multa() {
    }

    public Multa(Asignacion asignacion, LocalDate periodoInicio, LocalDate periodoFin,
                 int meta, int cumplidos, int monto) {
        this.asignacion = asignacion;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.meta = meta;
        this.cumplidos = cumplidos;
        this.monto = monto;
        this.estado = EstadoMulta.PENDIENTE;
    }

    @PrePersist
    void alCrear() {
        this.creadaEn = Instant.now();
    }

    // RN-17 y RN-19: no se borra, se anula dejando constancia de quién y por qué
    public void anular(Usuario admin, String motivo) {
        this.estado = EstadoMulta.ANULADA;
        this.anuladaPor = admin;
        this.anuladaEn = Instant.now();
        this.motivoAnulacion = motivo;
    }

    // RN-20: se marca pagada cuando los pagos confirmados alcanzan el monto
    public void marcarPagada() {
        this.estado = EstadoMulta.PAGADA;
    }

    public boolean perteneceA(Long usuarioId) {
        return asignacion.getUsuario().getId().equals(usuarioId);
    }

    public Long getGrupoId() {
        return asignacion.getHabito().getGrupo().getId();
    }

    public int getFaltantes() {
        return Math.max(0, meta - cumplidos);
    }

    public Long getId() { return id; }
    public Asignacion getAsignacion() { return asignacion; }
    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public LocalDate getPeriodoFin() { return periodoFin; }
    public int getMeta() { return meta; }
    public int getCumplidos() { return cumplidos; }
    public int getMonto() { return monto; }
    public EstadoMulta getEstado() { return estado; }
    public String getMotivoAnulacion() { return motivoAnulacion; }
    public Usuario getAnuladaPor() { return anuladaPor; }
    public Instant getAnuladaEn() { return anuladaEn; }
    public Instant getCreadaEn() { return creadaEn; }
}