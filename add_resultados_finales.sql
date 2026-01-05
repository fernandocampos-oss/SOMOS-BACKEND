-- Script para agregar tabla resultados_finales a la BD local
-- Ejecutar en PostgreSQL (base de datos GDR)

CREATE TABLE IF NOT EXISTS resultados_finales (
    id SERIAL PRIMARY KEY,
    id_votante BIGINT NOT NULL,
    anio INTEGER NOT NULL,
    rendimiento_distinguido VARCHAR(10),
    acciones_capacitacion TEXT,
    otras_acciones TEXT,
    fecha_reunion DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_votante, anio)
);

CREATE INDEX IF NOT EXISTS idx_resultados_finales_votante_anio 
ON resultados_finales(id_votante, anio);

COMMENT ON TABLE resultados_finales IS 'Tabla para almacenar los resultados finales de evaluación por trabajador y año';
COMMENT ON COLUMN resultados_finales.id_votante IS 'ID del trabajador evaluado';
COMMENT ON COLUMN resultados_finales.anio IS 'Año de la evaluación';
COMMENT ON COLUMN resultados_finales.rendimiento_distinguido IS 'Si/No - Rendimiento distinguido otorgado';
COMMENT ON COLUMN resultados_finales.acciones_capacitacion IS 'Acciones de capacitación propuestas';
COMMENT ON COLUMN resultados_finales.otras_acciones IS 'Otras acciones para mejora del desempeño';
COMMENT ON COLUMN resultados_finales.fecha_reunion IS 'Fecha de reunión de retroalimentación final';
