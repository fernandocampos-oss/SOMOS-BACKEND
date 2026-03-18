package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.MaestroGdr;
import pe.gob.essalud.apps.service.gdr.MaestroGdrService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("maestro-gdr")
@RequiredArgsConstructor
public class MaestroGdrController {

    private final MaestroGdrService maestroGdrService;

    /**
     * Verificar si el usuario actual es Maestro GDR
     * GET /maestro-gdr/es-maestro
     */
    @GetMapping("/es-maestro")
    public ResponseEntity<Map<String, Object>> esMaestroGdr() {
        Map<String, Object> response = new HashMap<>();
        boolean esMaestro = maestroGdrService.esMaestroGdr();
        response.put("esMaestroGdr", esMaestro);
        response.put("dni", maestroGdrService.obtenerDniUsuarioActual());
        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos los Maestros GDR activos
     * GET /maestro-gdr/listar
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarActivos() {
        if (!maestroGdrService.esMaestroGdr()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "No tiene permisos de Maestro GDR");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        List<MaestroGdr> lista = maestroGdrService.listarActivos();
        return ResponseEntity.ok(lista);
    }

    /**
     * Listar todos los Maestros GDR (activos e inactivos)
     * GET /maestro-gdr/listar-todos
     */
    @GetMapping("/listar-todos")
    public ResponseEntity<?> listarTodos() {
        if (!maestroGdrService.esMaestroGdr()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "No tiene permisos de Maestro GDR");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        List<MaestroGdr> lista = maestroGdrService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    /**
     * Agregar un nuevo Maestro GDR
     * POST /maestro-gdr/agregar
     * Body: { "dni": "12345678" }
     */
    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregar(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!maestroGdrService.esMaestroGdr()) {
                response.put("success", false);
                response.put("error", "No tiene permisos de Maestro GDR");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            String dni = body.get("dni");
            if (dni == null || dni.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "DNI es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            MaestroGdr maestro = maestroGdrService.agregar(dni.trim());
            response.put("success", true);
            response.put("maestro", maestro);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error agregando Maestro GDR: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Desactivar un Maestro GDR
     * PUT /maestro-gdr/desactivar/{dni}
     */
    @PutMapping("/desactivar/{dni}")
    public ResponseEntity<Map<String, Object>> desactivar(@PathVariable String dni) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!maestroGdrService.esMaestroGdr()) {
                response.put("success", false);
                response.put("error", "No tiene permisos de Maestro GDR");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            boolean resultado = maestroGdrService.desactivar(dni);
            response.put("success", resultado);
            if (!resultado) {
                response.put("message", "No se encontró el Maestro GDR o ya está inactivo");
            }
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error desactivando Maestro GDR: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Activar un Maestro GDR
     * PUT /maestro-gdr/activar/{dni}
     */
    @PutMapping("/activar/{dni}")
    public ResponseEntity<Map<String, Object>> activar(@PathVariable String dni) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!maestroGdrService.esMaestroGdr()) {
                response.put("success", false);
                response.put("error", "No tiene permisos de Maestro GDR");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            boolean resultado = maestroGdrService.activar(dni);
            response.put("success", resultado);
            if (!resultado) {
                response.put("message", "No se encontró el Maestro GDR");
            }
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error activando Maestro GDR: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Estadísticas de Maestros GDR
     * GET /maestro-gdr/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> estadisticas() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalActivos", maestroGdrService.contarActivos());
        response.put("total", maestroGdrService.listarTodos().size());
        return ResponseEntity.ok(response);
    }
}
