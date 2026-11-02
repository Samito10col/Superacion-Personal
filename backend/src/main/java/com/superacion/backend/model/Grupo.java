package com.superacion.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    protected Grupo() {
    }

    public Grupo(String nombre, String descripcion, Usuario creadoPor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Usuario getCreadoPor() { return creadoPor; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}