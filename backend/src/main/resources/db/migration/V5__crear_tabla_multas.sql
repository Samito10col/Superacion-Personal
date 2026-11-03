-- Hasta que fecha ya se evaluo cada asignacion (evita evaluar dos veces el mismo periodo)
ALTER TABLE asignaciones ADD COLUMN evaluada_hasta DATE;

-- Multas generadas al cerrar periodos (RN-14 a RN-19)
CREATE TABLE multas (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    asignacion_id     BIGINT       NOT NULL REFERENCES asignaciones(id),
    periodo_inicio    DATE         NOT NULL,
    periodo_fin       DATE         NOT NULL,
    meta              INT          NOT NULL,
    cumplidos         INT          NOT NULL,
    monto             INT          NOT NULL CHECK (monto > 0),
    estado            VARCHAR(10)  NOT NULL CHECK (estado IN ('PENDIENTE', 'PAGADA', 'ANULADA')),
    motivo_anulacion  VARCHAR(500),
    anulada_por       BIGINT       REFERENCES usuarios(id),
    anulada_en        TIMESTAMPTZ,
    creada_en         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    -- Una sola multa por asignacion y periodo
    CONSTRAINT uk_multas_periodo UNIQUE (asignacion_id, periodo_inicio)
);

CREATE INDEX idx_multas_estado ON multas (estado);
