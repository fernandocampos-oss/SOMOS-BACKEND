-- =============================================================================
-- SQL PARA CONFIGURACIÓN DE BD LOCAL Y DATOS DE PRUEBA
-- Base de Datos: GDR (PostgreSQL)
-- =============================================================================

-- 1. CREAR BASE DE DATOS (ejecutar como superusuario)
-- =============================================================================
-- CREATE DATABASE gdr
--     WITH 
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'Spanish_Peru.1252'
--     LC_CTYPE = 'Spanish_Peru.1252'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;


-- 2. CREAR TABLA SENTIDO_INDICADOR
-- =============================================================================
CREATE TABLE IF NOT EXISTS public.sentido_indicador (
    id SERIAL PRIMARY KEY,
    id_indicador BIGINT NOT NULL,
    sentido VARCHAR(20) NOT NULL CHECK (sentido IN ('ascendente', 'descendente')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sentido_indicador_id_indicador UNIQUE (id_indicador)
);

-- Índice para búsquedas rápidas por id_indicador
CREATE INDEX IF NOT EXISTS idx_sentido_id_indicador 
ON public.sentido_indicador(id_indicador);

-- Comentarios
COMMENT ON TABLE public.sentido_indicador IS 'Tabla para almacenar el sentido de cada indicador (ascendente/descendente)';
COMMENT ON COLUMN public.sentido_indicador.id_indicador IS 'ID del indicador en la BD principal';
COMMENT ON COLUMN public.sentido_indicador.sentido IS 'Dirección del sentido: ascendente o descendente';


-- 3. INSERTAR DATOS DE PRUEBA
-- =============================================================================
-- NOTA: Reemplaza los id_indicador con los IDs reales de tus indicadores
-- Puedes obtenerlos de tu sistema actual consultando la tabla de indicadores

-- Ejemplos con IDs hipotéticos (ajusta según tus datos reales):

INSERT INTO public.sentido_indicador (id_indicador, sentido) VALUES
(1, 'ascendente'),   -- Ajusta el ID según tu BD
(2, 'descendente'),
(3, 'ascendente'),
(4, 'ascendente'),
(5, 'descendente')
ON CONFLICT (id_indicador) DO UPDATE 
SET sentido = EXCLUDED.sentido,
    fecha_modificacion = CURRENT_TIMESTAMP;


-- 4. CONSULTAS ÚTILES PARA OBTENER IDs DE INDICADORES
-- =============================================================================
-- Si ya tienes indicadores creados, usa estas queries para obtener sus IDs:

-- Ver todos los indicadores (ajusta el nombre de la tabla según tu BD principal)
-- SELECT id_indicador, nombre_indicador, codigo_indicador 
-- FROM tu_schema.indicadores 
-- ORDER BY id_indicador;


-- 5. SCRIPT PARA ASIGNAR SENTIDO A INDICADORES EXISTENTES
-- =============================================================================
-- Opción A: Asignar sentido 'ascendente' a todos por defecto
-- INSERT INTO public.sentido_indicador (id_indicador, sentido)
-- SELECT id_indicador, 'ascendente'
-- FROM tu_schema.indicadores
-- WHERE NOT EXISTS (
--     SELECT 1 FROM public.sentido_indicador si 
--     WHERE si.id_indicador = indicadores.id_indicador
-- );

-- Opción B: Asignar basado en criterios específicos
-- Por ejemplo, si tienes un campo que indica el tipo:
-- INSERT INTO public.sentido_indicador (id_indicador, sentido)
-- SELECT 
--     id_indicador,
--     CASE 
--         WHEN tipo_indicador = 'EFICIENCIA' THEN 'ascendente'
--         WHEN tipo_indicador = 'ERRORES' THEN 'descendente'
--         ELSE 'ascendente'
--     END as sentido
-- FROM tu_schema.indicadores;


-- 6. QUERIES DE VALIDACIÓN
-- =============================================================================
-- Ver todos los sentidos configurados
SELECT * FROM public.sentido_indicador ORDER BY id_indicador;

-- Contar indicadores por sentido
SELECT sentido, COUNT(*) as cantidad
FROM public.sentido_indicador
GROUP BY sentido;

-- Ver últimas modificaciones
SELECT * FROM public.sentido_indicador 
ORDER BY fecha_modificacion DESC 
LIMIT 10;


-- 7. SCRIPT PARA EXPORTAR A PRODUCCIÓN
-- =============================================================================
-- Cuando estés listo para migrar a producción, exporta los datos:
/*
COPY (
    SELECT id_indicador, sentido 
    FROM public.sentido_indicador 
    ORDER BY id_indicador
) TO 'C:/temp/sentido_indicador_export.csv' 
WITH CSV HEADER;
*/

-- O genera el script de INSERT:
/*
SELECT 
    'INSERT INTO public.sentido_indicador (id_indicador, sentido) VALUES (' || 
    id_indicador || ', ''' || sentido || ''');' as script_insert
FROM public.sentido_indicador
ORDER BY id_indicador;
*/


-- 8. LIMPIEZA (SOLO SI NECESITAS REINICIAR)
-- =============================================================================
-- ¡CUIDADO! Esto eliminará todos los datos
-- TRUNCATE TABLE public.sentido_indicador RESTART IDENTITY CASCADE;

-- Eliminar tabla completamente
-- DROP TABLE IF EXISTS public.sentido_indicador CASCADE;
