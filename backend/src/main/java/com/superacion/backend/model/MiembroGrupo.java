package com.superacion.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "miembros_grupo")
public class MiembroGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolGrupo rol;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "unido_en", nullable = false)
    private Instant unidoEn;

    protected MiembroGrupo() {
    }

    public MiembroGrupo(Grupo grupo, Usuario usuario, RolGrupo rol) {
        this.grupo = grupo;
        this.usuario = usuario;
        this.rol = rol;
    }

    @PrePersist
    void alCrear() {
        if (this.unidoEn == null) {
            this.unidoEn = Instant.now();
        }
    }

    public boolean esAdmin() {
        return rol == RolGrupo.ADMIN;
    }

    // Vuelve a unir a alguien que había salido del grupo
    public void reactivar() {
        this.activo = true;
        this.rol = RolGrupo.MIEMBRO;
        this.unidoEn = Instant.now();
    }

    public Long getId() { return id; }
    public Grupo getGrupo() { return grupo; }
    public Usuario getUsuario() { return usuario; }
    public RolGrupo getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public Instant getUnidoEn() { return unidoEn; }
}