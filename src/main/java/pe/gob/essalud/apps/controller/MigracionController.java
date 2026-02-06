package pe.gob.essalud.apps.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * TODO: ELIMINAR DESPUÉS DE EJECUTAR EN PRODUCCIÓN
 * Controller temporal para ejecutar migraciones de BD
 * ================================================================
 */
@Slf4j
@RestController
@RequestMapping("migracion")
public class MigracionController {

    private final DataSource gdrDataSource;

    @Autowired
    public MigracionController(@Qualifier("gdrDataSource") DataSource gdrDataSource) {
        this.gdrDataSource = gdrDataSource;
    }

    /**
     * GET /api/migracion/test
     * Verifica conexión a BD
     */
    @GetMapping("/test")
    public ResponseEntity<MigracionResponse> test() {
        List<String> resultados = new ArrayList<>();
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(gdrDataSource);
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            resultados.add("OK Conexion a BD exitosa");
            
            // Mostrar URL de conexión
            try {
                String url = gdrDataSource.getConnection().getMetaData().getURL();
                resultados.add("BD URL: " + url);
            } catch (Exception e) {
                resultados.add("No se pudo obtener URL");
            }
            
            return ResponseEntity.ok(new MigracionResponse("OK", "Conexion exitosa", resultados));
        } catch (Exception e) {
            resultados.add("ERROR: " + e.getMessage());
            return ResponseEntity.ok(new MigracionResponse("ERROR", e.getMessage(), resultados));
        }
    }

    /**
     * POST /api/migracion/crear-tablas-gdr
     * Crea las 5 tablas nuevas
     */
    @PostMapping("/crear-tablas-gdr")
    public ResponseEntity<MigracionResponse> crearTablasGdr() {
        List<String> resultados = new ArrayList<>();
        int exitosas = 0;
        int fallidas = 0;
        
        JdbcTemplate jdbcTemplate;
        try {
            jdbcTemplate = new JdbcTemplate(gdrDataSource);
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            resultados.add("OK Conexion a BD exitosa");
        } catch (Exception e) {
            log.error("Error conectando a BD", e);
            resultados.add("ERROR Conexion BD: " + e.getMessage());
            return ResponseEntity.ok(new MigracionResponse("ERROR", "No se pudo conectar", resultados));
        }

        // 1. Tabla sentido_indicador
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS sentido_indicador (" +
                "id SERIAL PRIMARY KEY, " +
                "id_indicador INTEGER NOT NULL, " +
                "sentido VARCHAR(20) NOT NULL, " +
                "fecha_creacion TIMESTAMP DEFAULT NOW(), " +
                "fecha_modificacion TIMESTAMP DEFAULT NOW())");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_sentido_id_indicador ON sentido_indicador(id_indicador)");
            resultados.add("OK sentido_indicador: CREADA");
            exitosas++;
        } catch (Exception e) {
            resultados.add("ERROR sentido_indicador: " + e.getMessage());
            fallidas++;
        }

        // 2. Tabla evidencia_tipo
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS evidencia_tipo (" +
                "id SERIAL PRIMARY KEY, " +
                "id_evidencia INTEGER NOT NULL UNIQUE, " +
                "id_indicador INTEGER NOT NULL, " +
                "tipo VARCHAR(20) NOT NULL, " +
                "orden INTEGER NOT NULL, " +
                "fecha_plazo DATE, " +
                "fecha_creacion TIMESTAMP DEFAULT NOW(), " +
                "fecha_modificacion TIMESTAMP DEFAULT NOW())");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_evidencia_tipo_indicador ON evidencia_tipo(id_indicador)");
            resultados.add("OK evidencia_tipo: CREADA");
            exitosas++;
        } catch (Exception e) {
            resultados.add("ERROR evidencia_tipo: " + e.getMessage());
            fallidas++;
        }

        // 3. Tabla comentario_estado
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS comentario_estado (" +
                "id SERIAL PRIMARY KEY, " +
                "id_evidencia INTEGER NOT NULL UNIQUE, " +
                "estado_dropdown VARCHAR(50), " +
                "comentario_adicional TEXT, " +
                "fecha_creacion TIMESTAMP DEFAULT NOW(), " +
                "fecha_modificacion TIMESTAMP DEFAULT NOW())");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_comentario_estado_evidencia ON comentario_estado(id_evidencia)");
            resultados.add("OK comentario_estado: CREADA");
            exitosas++;
        } catch (Exception e) {
            resultados.add("ERROR comentario_estado: " + e.getMessage());
            fallidas++;
        }

        // 4. Tabla valor_alcanzado_prioridad
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS valor_alcanzado_prioridad (" +
                "id SERIAL PRIMARY KEY, " +
                "id_prioridad INTEGER NOT NULL UNIQUE, " +
                "valor_alcanzado DECIMAL(10,2), " +
                "fecha_creacion TIMESTAMP DEFAULT NOW(), " +
                "fecha_modificacion TIMESTAMP DEFAULT NOW())");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_valor_alcanzado_prioridad ON valor_alcanzado_prioridad(id_prioridad)");
            resultados.add("OK valor_alcanzado_prioridad: CREADA");
            exitosas++;
        } catch (Exception e) {
            resultados.add("ERROR valor_alcanzado_prioridad: " + e.getMessage());
            fallidas++;
        }

        // 5. Tabla resultados_finales
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS resultados_finales (" +
                "id SERIAL PRIMARY KEY, " +
                "id_votante BIGINT NOT NULL, " +
                "anio INTEGER NOT NULL, " +
                "rendimiento_distinguido VARCHAR(10), " +
                "acciones_capacitacion TEXT, " +
                "otras_acciones TEXT, " +
                "fecha_reunion DATE, " +
                "fecha_creacion TIMESTAMP DEFAULT NOW(), " +
                "fecha_modificacion TIMESTAMP DEFAULT NOW(), " +
                "UNIQUE(id_votante, anio))");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_resultados_finales_votante_anio ON resultados_finales(id_votante, anio)");
            resultados.add("OK resultados_finales: CREADA");
            exitosas++;
        } catch (Exception e) {
            resultados.add("ERROR resultados_finales: " + e.getMessage());
            fallidas++;
        }

        log.info("Migración completada: {} exitosas, {} fallidas", exitosas, fallidas);
        
        return ResponseEntity.ok(new MigracionResponse(
            exitosas == 5 ? "COMPLETADO" : "PARCIAL",
            "Tablas: " + exitosas + " exitosas, " + fallidas + " fallidas",
            resultados
        ));
    }

    /**
     * GET /api/migracion/verificar-tablas
     * Verifica qué tablas existen
     */
    @GetMapping("/verificar-tablas")
    public ResponseEntity<MigracionResponse> verificarTablas() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(gdrDataSource);
        List<String> resultados = new ArrayList<>();
        String[] tablas = {"sentido_indicador", "evidencia_tipo", "comentario_estado", 
                          "valor_alcanzado_prioridad", "resultados_finales"};

        for (String tabla : tablas) {
            try {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?",
                    Integer.class, tabla);
                if (count != null && count > 0) {
                    resultados.add("OK " + tabla + ": EXISTE");
                } else {
                    resultados.add("FALTA " + tabla + ": NO EXISTE");
                }
            } catch (Exception e) {
                resultados.add("WARN " + tabla + ": Error - " + e.getMessage());
            }
        }

        return ResponseEntity.ok(new MigracionResponse("INFO", "Estado de tablas", resultados));
    }

    @Data
    @AllArgsConstructor
    public static class MigracionResponse {
        private String estado;
        private String mensaje;
        private List<String> detalles;
    }
}
