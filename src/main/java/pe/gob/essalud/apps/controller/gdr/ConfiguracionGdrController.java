package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.ConfiguracionGdr;
import pe.gob.essalud.apps.service.gdr.ConfiguracionGdrService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gestionar la Configuración del ciclo GDR.
 * Solo el Maestro GDR puede modificar esta configuración.
 * 
 * Endpoints:
 * - GET /gdr/configuracion/{periodo}: Obtiene o crea configuración para un periodo
 * - PUT /gdr/configuracion/{periodo}/fases: Actualiza las fases
 * - PUT /gdr/configuracion/{periodo}/evidencias: Actualiza las evidencias
 * - PUT /gdr/configuracion/{periodo}: Actualiza toda la configuración
 * - GET /gdr/configuracion/{periodo}/fase/{fase}: Verifica si una fase está activa
 * - GET /gdr/configuracion/{periodo}/evidencia/{numero}: Verifica si una evidencia está activa
 */
@RestController
@RequestMapping("/gdr/configuracion")
@RequiredArgsConstructor
@Slf4j
public class ConfiguracionGdrController {

    private final ConfiguracionGdrService configuracionService;

    /**
     * Obtener o crear configuración para un periodo
     */
    @GetMapping("/{periodo}")
    public ResponseEntity<?> obtenerOCrear(@PathVariable String periodo) {
        try {
            ConfiguracionGdr config = configuracionService.obtenerOCrear(periodo);
            return ResponseEntity.ok(convertirAMapa(config));
        } catch (Exception e) {
            log.error("Error al obtener configuración: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar fases, para activar
     * 
     * Request body:
     * {
     *   "fasePreActiva": false,
     *   "fasePlanificacionActiva": true,
     *   "faseSeguimientoActiva": false,
     *   "faseEvaluacionActiva": false,
     *   "fasePostActiva": false,
     *   "modificadoPor": "70946713"
     * }
     */
    @PutMapping("/{periodo}/fases")
    public ResponseEntity<?> actualizarFases(
            @PathVariable String periodo,
            @RequestBody Map<String, Object> request) {
        try {
            Boolean fasePreActiva = request.get("fasePreActiva") != null ? 
                    Boolean.valueOf(request.get("fasePreActiva").toString()) : null;
            Boolean fasePlanificacionActiva = request.get("fasePlanificacionActiva") != null ? 
                    Boolean.valueOf(request.get("fasePlanificacionActiva").toString()) : null;
            Boolean faseSeguimientoActiva = request.get("faseSeguimientoActiva") != null ? 
                    Boolean.valueOf(request.get("faseSeguimientoActiva").toString()) : null;
            Boolean faseEvaluacionActiva = request.get("faseEvaluacionActiva") != null ? 
                    Boolean.valueOf(request.get("faseEvaluacionActiva").toString()) : null;
            Boolean fasePostActiva = request.get("fasePostActiva") != null ? 
                    Boolean.valueOf(request.get("fasePostActiva").toString()) : null;
            String modificadoPor = request.get("modificadoPor") != null ? 
                    request.get("modificadoPor").toString() : null;

            ConfiguracionGdr config = configuracionService.actualizarFases(
                    periodo, fasePreActiva, fasePlanificacionActiva,
                    faseSeguimientoActiva, faseEvaluacionActiva,
                    fasePostActiva, modificadoPor);
            
            return ResponseEntity.ok(convertirAMapa(config));
        } catch (Exception e) {
            log.error("Error al actualizar fases: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar evidencias
     * 
     * Request body:
     * {
     *   "evidencia1Activa": true,
     *   "evidencia2Activa": false,
     *   "evidenciaFinalActiva": false,
     *   "modificadoPor": "70946713"
     * }
     */
    @PutMapping("/{periodo}/evidencias")
    public ResponseEntity<?> actualizarEvidencias(
            @PathVariable String periodo,
            @RequestBody Map<String, Object> request) {
        try {
            Boolean evidencia1Activa = request.get("evidencia1Activa") != null ? 
                    Boolean.valueOf(request.get("evidencia1Activa").toString()) : null;
            Boolean evidencia2Activa = request.get("evidencia2Activa") != null ? 
                    Boolean.valueOf(request.get("evidencia2Activa").toString()) : null;
            Boolean evidenciaFinalActiva = request.get("evidenciaFinalActiva") != null ? 
                    Boolean.valueOf(request.get("evidenciaFinalActiva").toString()) : null;
            String modificadoPor = request.get("modificadoPor") != null ? 
                    request.get("modificadoPor").toString() : null;

            ConfiguracionGdr config = configuracionService.actualizarEvidencias(
                    periodo, evidencia1Activa, evidencia2Activa,
                    evidenciaFinalActiva, modificadoPor);
            
            return ResponseEntity.ok(convertirAMapa(config));
        } catch (Exception e) {
            log.error("Error al actualizar evidencias: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar toda la configuración
     * 
     * Request body:
     * {
     *   "fasePreActiva": false,
     *   "fasePlanificacionActiva": true,
     *   "faseSeguimientoActiva": false,
     *   "faseEvaluacionActiva": false,
     *   "fasePostActiva": false,
     *   "evidencia1Activa": true,
     *   "evidencia2Activa": false,
     *   "evidenciaFinalActiva": false,
     *   "modificadoPor": "70946713"
     * }
     */
    @PutMapping("/{periodo}")
    public ResponseEntity<?> actualizarConfiguracion(
            @PathVariable String periodo,
            @RequestBody Map<String, Object> request) {
        try {
            ConfiguracionGdr configActualizada = new ConfiguracionGdr(periodo);
            
            configActualizada.setFasePreActiva(
                    request.get("fasePreActiva") != null ? 
                    Boolean.valueOf(request.get("fasePreActiva").toString()) : false);
            configActualizada.setFasePlanificacionActiva(
                    request.get("fasePlanificacionActiva") != null ? 
                    Boolean.valueOf(request.get("fasePlanificacionActiva").toString()) : true);
            configActualizada.setFaseSeguimientoActiva(
                    request.get("faseSeguimientoActiva") != null ? 
                    Boolean.valueOf(request.get("faseSeguimientoActiva").toString()) : false);
            configActualizada.setFaseEvaluacionActiva(
                    request.get("faseEvaluacionActiva") != null ? 
                    Boolean.valueOf(request.get("faseEvaluacionActiva").toString()) : false);
            configActualizada.setFasePostActiva(
                    request.get("fasePostActiva") != null ? 
                    Boolean.valueOf(request.get("fasePostActiva").toString()) : false);
            
            configActualizada.setEvidencia1Activa(
                    request.get("evidencia1Activa") != null ? 
                    Boolean.valueOf(request.get("evidencia1Activa").toString()) : false);
            configActualizada.setEvidencia2Activa(
                    request.get("evidencia2Activa") != null ? 
                    Boolean.valueOf(request.get("evidencia2Activa").toString()) : false);
            configActualizada.setEvidenciaFinalActiva(
                    request.get("evidenciaFinalActiva") != null ? 
                    Boolean.valueOf(request.get("evidenciaFinalActiva").toString()) : false);
            
            String modificadoPor = request.get("modificadoPor") != null ? 
                    request.get("modificadoPor").toString() : null;

            ConfiguracionGdr config = configuracionService.actualizarConfiguracion(
                    configActualizada, modificadoPor);
            
            return ResponseEntity.ok(convertirAMapa(config));
        } catch (Exception e) {
            log.error("Error al actualizar configuración: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verificar si una fase está activa
     */
    @GetMapping("/{periodo}/fase/{fase}")
    public ResponseEntity<?> verificarFase(
            @PathVariable String periodo,
            @PathVariable String fase) {
        try {
            boolean activa = configuracionService.esFaseActiva(periodo, fase);
            return ResponseEntity.ok(Map.of(
                    "periodo", periodo,
                    "fase", fase.toUpperCase(),
                    "activa", activa
            ));
        } catch (Exception e) {
            log.error("Error al verificar fase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verificar si una evidencia está activa
     * numeroEvidencia: 1 = Evidencia 1, 2 = Evidencia 2, 3 = Evidencia Final
     * maximo solo puede tener 2 evidencia inicial y 1 final
     */
    @GetMapping("/{periodo}/evidencia/{numero}")
    public ResponseEntity<?> verificarEvidencia(
            @PathVariable String periodo,
            @PathVariable int numero) {
        try {
            boolean activa = configuracionService.esEvidenciaActiva(periodo, numero);
            String nombreEvidencia = numero == 1 ? "EVIDENCIA_1" : 
                                     numero == 2 ? "EVIDENCIA_2" : "EVIDENCIA_FINAL";
            return ResponseEntity.ok(Map.of(
                    "periodo", periodo,
                    "evidencia", nombreEvidencia,
                    "activa", activa
            ));
        } catch (Exception e) {
            log.error("Error al verificar evidencia: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Convertir entidad a Map para respuesta JSON
     */
    private Map<String, Object> convertirAMapa(ConfiguracionGdr config) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("idConfiguracion", config.getIdConfiguracion());
        mapa.put("periodo", config.getPeriodo());
        
        // Fases
        mapa.put("fasePreActiva", config.getFasePreActiva());
        mapa.put("fasePlanificacionActiva", config.getFasePlanificacionActiva());
        mapa.put("faseSeguimientoActiva", config.getFaseSeguimientoActiva());
        mapa.put("faseEvaluacionActiva", config.getFaseEvaluacionActiva());
        mapa.put("fasePostActiva", config.getFasePostActiva());
        
        // Evidencias
        mapa.put("evidencia1Activa", config.getEvidencia1Activa());
        mapa.put("evidencia2Activa", config.getEvidencia2Activa());
        mapa.put("evidenciaFinalActiva", config.getEvidenciaFinalActiva());
        
        // Auditoría
        mapa.put("modificadoPor", config.getModificadoPor());
        mapa.put("fechaModificacion", config.getFechaModificacion() != null ? 
                config.getFechaModificacion().toString() : null);
        
        return mapa;
    }
}
