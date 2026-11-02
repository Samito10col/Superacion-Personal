-- Habitos que un grupo se compromete a cumplir (RN-04 a RN-06)
CREATE TABLE habitos (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    grupo_id           BIGINT       NOT NULL REFERENCES grupos(id),
    nombre             VARCHAR(100) NOT NULL,
    descripcion        VARCHAR(500),
    frecuencia         VARCHAR(10)  NOT NULL CHECK (frecuencia IN ('DIARIA', 'SEMANAL', 'MENSUAL')),
    meta_por_periodo   INT          NOT NULL CHECK (meta_por_periodo >= 1),
    tipo_multa         VARCHAR(10)  NOT NULL CHECK (tipo_multa IN ('FIJA', 'POR_FALTA')),
    valor_multa        INT          NOT NULL CHECK (valor_multa > 0),
    modo_verificacion  VARCHAR(30)  NOT NULL CHECK (modo_verificacion IN
                           ('AUTODECLARADO', 'EVIDENCIA', 'VALIDACION_PAR', 'EVIDENCIA_Y_VALIDACION')),
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_por         BIGINT       NOT NULL REFERENCES usuarios(id),
    creado_en          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    actualizado_en     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_habitos_grupo ON habitos (grupo_id);

-- Quien debe cumplir cada habito y desde que fecha (RN-07)
CREATE TABLE asignaciones (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    habito_id      BIGINT      NOT NULL REFERENCES habitos(id),
    usuario_id     BIGINT      NOT NULL REFERENCES usuarios(id),
    activa         BOOLEAN     NOT NULL DEFAULT TRUE,
    vigente_desde  DATE        NOT NULL,
    asignada_en    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_asignaciones UNIQUE (habito_id, usuario_id)
);

CREATE INDEX idx_asignaciones_usuario ON asignaciones (usuario_id);
