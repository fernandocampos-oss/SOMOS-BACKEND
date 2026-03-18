package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.SegmentoGdr;
import pe.gob.essalud.apps.service.gdr.SegmentoGdrService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("segmento-gdr")
@RequiredArgsConstructor
public class SegmentoGdrController {

    private final SegmentoGdrService segmentoGdrService;

    /**
     * Listar todos los segmentos
     * GET /segmento-gdr/listar
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listar() {
        if (!segmentoGdrService.tieneAcceso()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "error", "No tiene permisos de Maestro GDR"));
        }
        List<SegmentoGdr> lista = segmentoGdrService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    /**
     * Buscar por DNI (parcial)
     * GET /segmento-gdr/buscar?dni=12345
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam(required = false) String dni) {
        if (!segmentoGdrService.tieneAcceso()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "error", "No tiene permisos de Maestro GDR"));
        }
        List<SegmentoGdr> lista = segmentoGdrService.buscarPorDni(dni);
        return ResponseEntity.ok(lista);
    }

    /**
     * Obtener segmentos válidos
     * GET /segmento-gdr/segmentos-validos
     */
    @GetMapping("/segmentos-validos")
    public ResponseEntity<?> segmentosValidos() {
        return ResponseEntity.ok(segmentoGdrService.getSegmentosValidos());
    }

    /**
     * Agregar nuevo segmento
     * POST /segmento-gdr/agregar
     * Body: { "dni": "12345678", "segmento": "Directivo" }
     */
    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregar(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!segmentoGdrService.tieneAcceso()) {
                response.put("success", false);
                response.put("error", "No tiene permisos de Maestro GDR");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            String dni = body.get("dni");
            String segmento = body.get("segmento");
            
            if (dni == null || dni.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "DNI es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (segmento == null || segmento.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Segmento es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            SegmentoGdr creado = segmentoGdrService.agregar(dni.trim(), segmento.trim());
            response.put("success", true);
            response.put("registro", creado);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error agregando segmento: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Actualizar segmento existente
     * PUT /segmento-gdr/actualizar
     * Body: { "dni": "12345678", "segmento": "Ejecutor" }
     */
    @PutMapping("/actualizar")
    public ResponseEntity<Map<String, Object>> actualizar(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!segmentoGdrService.tieneAcceso()) {
                response.put("success", false);
                response.put("error", "No tiene permisos de Maestro GDR");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            String dni = body.get("dni");
            String segmento = body.get("segmento");
            
            if (dni == null || dni.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "DNI es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (segmento == null || segmento.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Segmento es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean actualizado = segmentoGdrService.actualizar(dni.trim(), segmento.trim());
            response.put("success", actualizado);
            if (!actualizado) {
                response.put("message", "No se encontró registro con el DNI especificado");
            }
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error actualizando segmento: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Eliminar segmento por DNI
     * DELETE /segmento-gdr/eliminar/{dni}
     */
    @DeleteMapping("/eliminar/{dni}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable String dni) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!segmentoGdrService.tieneAcceso()) {
                response.put("success", false);
                response.put("error", "No tiene permisos de Maestro GDR");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            boolean eliminado = segmentoGdrService.eliminar(dni);
            response.put("success", eliminado);
            if (!eliminado) {
                response.put("message", "No se encontró registro con el DNI especificado");
            }
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error eliminando segmento: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Carga masiva desde lista JSON
     * POST /segmento-gdr/carga-masiva
     * Body: { "registros": [{ "dni": "12345678", "segmento": "Directivo" }, ...] }
     */
    @PostMapping("/carga-masiva")
    public ResponseEntity<Map<String, Object>> cargaMasiva(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!segmentoGdrService.tieneAcceso()) {
                response.put("success", false);
                response.put("error", "No tiene permisos de Maestro GDR");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, String>> registros = (List<Map<String, String>>) body.get("registros");
            
            if (registros == null || registros.isEmpty()) {
                response.put("success", false);
                response.put("error", "No se proporcionaron registros");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> resultado = segmentoGdrService.cargaMasiva(registros);
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            log.error("Error en carga masiva: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Contar total de registros
     * GET /segmento-gdr/contar
     */
    @GetMapping("/contar")
    public ResponseEntity<?> contar() {
        if (!segmentoGdrService.tieneAcceso()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "error", "No tiene permisos de Maestro GDR"));
        }
        return ResponseEntity.ok(Map.of("total", segmentoGdrService.contar()));
    }
}
