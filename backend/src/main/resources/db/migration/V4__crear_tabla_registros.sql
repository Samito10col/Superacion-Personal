-- Registros de cumplimiento de habitos (RN-08 a RN-13)
CREATE TABLE registros (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    asignacion_id     BIGINT       NOT NULL REFERENCES asignaciones(id),
    fecha             DATE         NOT NULL,
    estado            VARCHAR(25)  NOT NULL CHECK (estado IN
                          ('APROBADO', 'PENDIENTE_VALIDACION', 'RECHAZADO', 'APELADO')),
    nota              VARCHAR(500),
    evidencia_ruta    VARCHAR(255),
    validado_por      BIGINT       REFERENCES usuarios(id),
    fecha_validacion  TIMESTAMPTZ,
    motivo_rechazo    VARCHAR(500),
    apelado           BOOLEAN      NOT NULL DEFAULT FALSE,
    creado_en         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    -- RN-09: maximo un registro por dia y por habito
    CONSTRAINT uk_registros_dia UNIQUE (asignacion_id, fecha)
);

CREATE INDEX idx_registros_estado ON registros (estado, creado_en);
