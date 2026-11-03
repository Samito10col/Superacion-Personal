-- Pagos de multas (RN-20 a RN-22)
CREATE TABLE pagos (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    multa_id        BIGINT       NOT NULL REFERENCES multas(id),
    monto           INT          NOT NULL CHECK (monto > 0),
    metodo          VARCHAR(20)  NOT NULL CHECK (metodo IN
                        ('EFECTIVO', 'TRANSFERENCIA', 'NEQUI', 'DAVIPLATA', 'OTRO')),
    referencia      VARCHAR(100),
    estado          VARCHAR(15)  NOT NULL CHECK (estado IN ('POR_CONFIRMAR', 'CONFIRMADO', 'RECHAZADO')),
    registrado_por  BIGINT       NOT NULL REFERENCES usuarios(id),
    revisado_por    BIGINT       REFERENCES usuarios(id),
    revisado_en     TIMESTAMPTZ,
    motivo_rechazo  VARCHAR(500),
    creado_en       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_pagos_multa ON pagos (multa_id);
CREATE INDEX idx_pagos_estado ON pagos (estado);
