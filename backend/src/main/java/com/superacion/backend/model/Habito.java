package com.superacion.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "habitos")
public class Habito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Frecuencia frecuencia;

    @Column(name = "meta_por_periodo", nullable = false)
    private int metaPorPeriodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_multa", nullable = false, length = 10)
    private TipoMulta tipoMulta;

    @Column(name = "valor_multa", nullable = false)
    private int valorMulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_verificacion", nullable = false, length = 30)
    private ModoVerificacion modoVerificacion;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    protected Habito() {
    }

    public Habito(Grupo grupo, String nombre, String descripcion, Frecuencia frecuencia,
                  int metaPorPeriodo, TipoMulta tipoMulta, int valorMulta,
                  ModoVerificacion modoVerificacion, Usuario creadoPor) {
        this.grupo = grupo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.frecuencia = frecuencia;
        this.metaPorPeriodo = metaPorPeriodo;
        this.tipoMulta = tipoMulta;
        this.valorMulta = valorMulta;
        this.modoVerificacion = modoVerificacion;
        this.creadoPor = creadoPor;
    }

    @PrePersist
    void alCrear() {
        Instant ahora = Instant.now();
        this.creadoEn = ahora;
        this.actualizadoEn = ahora;
    }

    @PreUpdate
    void alActualizar() {
        this.actualizadoEn = Instant.now();
    }

    public void desactivar() {
        this.activo = false;
    }

    public Long getId() { return id; }
    public Grupo getGrupo() { return grupo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Frecuencia getFrecuencia() { return frecuencia; }
    public int getMetaPorPeriodo() { return metaPorPeriodo; }
    public TipoMulta getTipoMulta() { return tipoMulta; }
    public int getValorMulta() { return valorMulta; }
    public ModoVerificacion getModoVerificacion() { return modoVerificacion; }
    public boolean isActivo() { return activo; }
    public Usuario getCreadoPor() { return creadoPor; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
