package com.superacion.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "multa_id", nullable = false)
    private Multa multa;

    @Column(nullable = false)
    private int monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodo;

    @Column(length = 100)
    private String referencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoPago estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registrado_por", nullable = false)
    private Usuario registradoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisado_por")
    private Usuario revisadoPor;

    @Column(name = "revisado_en")
    private Instant revisadoEn;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected Pago() {
    }

    public Pago(Multa multa, int monto, MetodoPago metodo, String referencia, Usuario registradoPor) {
        this.multa = multa;
        this.monto = monto;
        this.metodo = metodo;
        this.referencia = referencia;
        this.registradoPor = registradoPor;
        this.estado = EstadoPago.POR_CONFIRMAR;
    }

    @PrePersist
    void alCrear() {
        this.creadoEn = Instant.now();
    }

    // RN-21: el admin confirma que el dinero llegó
    public void confirmar(Usuario admin) {
        this.estado = EstadoPago.CONFIRMADO;
        this.revisadoPor = admin;
        this.revisadoEn = Instant.now();
    }

    public void rechazar(Usuario admin, String motivo) {
        this.estado = EstadoPago.RECHAZADO;
        this.revisadoPor = admin;
        this.revisadoEn = Instant.now();
        this.motivoRechazo = motivo;
    }

    public Long getId() { return id; }
    public Multa getMulta() { return multa; }
    public int getMonto() { return monto; }
    public MetodoPago getMetodo() { return metodo; }
    public String getReferencia() { return referencia; }
    public EstadoPago getEstado() { return estado; }
    public Usuario getRegistradoPor() { return registradoPor; }
    public Usuario getRevisadoPor() { return revisadoPor; }
    public Instant getRevisadoEn() { return revisadoEn; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public Instant getCreadoEn() { return creadoEn; }
}