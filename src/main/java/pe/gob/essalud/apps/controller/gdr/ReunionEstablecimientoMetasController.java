package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.ReunionEstablecimientoMetas;
import pe.gob.essalud.apps.service.gdr.ReunionEstablecimientoMetasService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestionar la Reunión de Establecimiento de Metas.
 * 
 * Endpoints:
 * - POST /gdr/reunion-metas/obtener-o-crear: Obtiene o crea un registro
 * - GET /gdr/reunion-metas/{id}: Obtiene reunión por ID
 * - GET /gdr/reunion-metas/buscar: Busca por evaluado/evaluador/periodo
 * - PUT /gdr/reunion-metas/{id}/asistencia: Actualiza asistencia
 * - PUT /gdr/reunion-metas/{id}/confirmar: Confirma la reunión
 * - PUT /gdr/reunion-metas/{id}/reiniciar: Reinicia confirmación (solo Maestro GDR)
 */
@RestController
@RequestMapping("/gdr/reunion-metas")
@RequiredArgsConstructor
@Slf4j
public class ReunionEstablecimientoMetasController {

    private final ReunionEstablecimientoMetasService reunionService;

    /**
     * Obtener o crear registro de reunión
     * 
     * Request body:
     * {
     *   "idVotanteEvaluado": 12345,
     *   "idVotanteEvaluador": 67890,
     *   "periodo": "2026"
     * }
     */
    @PostMapping("/obtener-o-crear")
    public ResponseEntity<?> obtenerOCrear(@RequestBody Map<String, Object> request) {
        try {
            Long idVotanteEvaluado = Long.valueOf(request.get("idVotanteEvaluado").toString());
            Long idVotanteEvaluador = Long.valueOf(request.get("idVotanteEvaluador").toString());
            String periodo = request.get("periodo").toString();

            ReunionEstablecimientoMetas reunion = reunionService.obtenerOCrear(
                    idVotanteEvaluado, idVotanteEvaluador, periodo);
            
            return ResponseEntity.ok(convertirAMapa(reunion));
        } catch (Exception e) {
            log.error("Error al obtener/crear reunión: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener reunión por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return reunionService.buscarPorId(id)
                .map(r -> ResponseEntity.ok(convertirAMapa(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Buscar reunión por parámetros
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam Long idVotanteEvaluado,
            @RequestParam Long idVotanteEvaluador,
            @RequestParam String periodo) {
        return reunionService.buscar(idVotanteEvaluado, idVotanteEvaluador, periodo)
                .map(r -> ResponseEntity.ok(convertirAMapa(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar reuniones de un evaluador
     */
    @GetMapping("/por-evaluador")
    public ResponseEntity<List<Map<String, Object>>> listarPorEvaluador(
            @RequestParam Long idVotanteEvaluador,
            @RequestParam String periodo) {
        List<ReunionEstablecimientoMetas> reuniones = reunionService.listarPorEvaluador(idVotanteEvaluador, periodo);
        List<Map<String, Object>> resultado = reuniones.stream()
                .map(this::convertirAMapa)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Listar reuniones de un evaluado (para búsqueda por Maestro GDR)
     */
    @GetMapping("/por-evaluado")
    public ResponseEntity<List<Map<String, Object>>> listarPorEvaluado(
            @RequestParam Long idVotanteEvaluado,
            @RequestParam String periodo) {
        List<ReunionEstablecimientoMetas> reuniones = reunionService.listarPorEvaluado(idVotanteEvaluado, periodo);
        List<Map<String, Object>> resultado = reuniones.stream()
                .map(this::convertirAMapa)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Actualizar asistencia
     * 
     * Request body:
     * {
     *   "asistio": "S",  // '-', 'S', 'N'
     *   "fechaReunion": "2026-03-06"  // Solo obligatorio si asistio = 'S'
     * }
     */
    @PutMapping("/{id}/asistencia")
    public ResponseEntity<?> actualizarAsistencia(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String asistio = request.get("asistio").toString();
            LocalDate fechaReunion = null;
            
            if (request.get("fechaReunion") != null && !request.get("fechaReunion").toString().isEmpty()) {
                fechaReunion = LocalDate.parse(request.get("fechaReunion").toString());
            }

            ReunionEstablecimientoMetas reunion = reunionService.actualizarAsistencia(id, asistio, fechaReunion);
            return ResponseEntity.ok(convertirAMapa(reunion));
        } catch (RuntimeException e) {
            log.error("Error al actualizar asistencia: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Confirmar la reunión
     */
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        try {
            ReunionEstablecimientoMetas reunion = reunionService.confirmar(id);
            return ResponseEntity.ok(convertirAMapa(reunion));
        } catch (RuntimeException e) {
            log.error("Error al confirmar reunión: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Reiniciar confirmación (solo Maestro GDR)
     * 
     * Request body:
     * {
     *   "dniMaestroGdr": "12345678"
     * }
     */
    @PutMapping("/{id}/reiniciar")
    public ResponseEntity<?> reiniciarConfirmacion(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String dniMaestroGdr = request.get("dniMaestroGdr").toString();
            ReunionEstablecimientoMetas reunion = reunionService.reiniciarConfirmacion(id, dniMaestroGdr);
            return ResponseEntity.ok(convertirAMapa(reunion));
        } catch (RuntimeException e) {
            log.error("Error al reiniciar confirmación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Verificar si ya está confirmado
     */
    @GetMapping("/esta-confirmado")
    public ResponseEntity<Map<String, Object>> estaConfirmado(
            @RequestParam Long idVotanteEvaluado,
            @RequestParam Long idVotanteEvaluador,
            @RequestParam String periodo) {
        boolean confirmado = reunionService.estaConfirmado(idVotanteEvaluado, idVotanteEvaluador, periodo);
        return ResponseEntity.ok(Map.of("confirmado", confirmado));
    }

    /**
     * Estadísticas por periodo
     */
    @GetMapping("/estadisticas/{periodo}")
    public ResponseEntity<Map<String, Object>> estadisticas(@PathVariable String periodo) {
        long confirmados = reunionService.contarConfirmados(periodo);
        long pendientes = reunionService.contarPendientes(periodo);
        return ResponseEntity.ok(Map.of(
                "periodo", periodo,
                "confirmados", confirmados,
                "pendientes", pendientes,
                "total", confirmados + pendientes
        ));
    }

    /**
     * Convertir entidad a mapa para respuesta JSON
     */
    private Map<String, Object> convertirAMapa(ReunionEstablecimientoMetas r) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("idReunion", r.getIdReunion());
        mapa.put("idVotanteEvaluado", r.getIdVotanteEvaluado());
        mapa.put("idVotanteEvaluador", r.getIdVotanteEvaluador());
        mapa.put("periodo", r.getPeriodo());
        mapa.put("asistio", r.getAsistio());
        mapa.put("fechaReunion", r.getFechaReunion());
        mapa.put("confirmado", r.getConfirmado());
        mapa.put("fechaConfirmacion", r.getFechaConfirmacion());
        mapa.put("reiniciadoPor", r.getReiniciadoPor());
        mapa.put("fechaReinicio", r.getFechaReinicio());
        mapa.put("fechaCreacion", r.getFechaCreacion());
        mapa.put("fechaModificacion", r.getFechaModificacion());
        // Campos de utilidad para el frontend
        mapa.put("puedeConfirmar", r.puedeConfirmar());
        mapa.put("estaConfirmado", r.estaConfirmado());
        return mapa;
    }
}
