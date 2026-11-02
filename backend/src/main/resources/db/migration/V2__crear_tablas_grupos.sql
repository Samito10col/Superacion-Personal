-- Grupos de personas que comparten habitos y multas
CREATE TABLE grupos (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    descripcion     VARCHAR(500),
    creado_por      BIGINT       NOT NULL REFERENCES usuarios(id),
    creado_en       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    actualizado_en  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Relacion usuario-grupo con su rol (RN-01, RN-02, RN-03)
CREATE TABLE miembros_grupo (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    grupo_id    BIGINT      NOT NULL REFERENCES grupos(id),
    usuario_id  BIGINT      NOT NULL REFERENCES usuarios(id),
    rol         VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'MIEMBRO')),
    activo      BOOLEAN     NOT NULL DEFAULT TRUE,
    unido_en    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_miembros_grupo UNIQUE (grupo_id, usuario_id)
);

CREATE INDEX idx_miembros_grupo_usuario ON miembros_grupo (usuario_id);
