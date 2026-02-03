package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.EvidenciaTipo;
import pe.gob.essalud.apps.service.gdr.EvidenciaTipoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gdr/evidencia-tipo")
@RequiredArgsConstructor
@Slf4j
public class EvidenciaTipoController {

    private final EvidenciaTipoService evidenciaTipoService;

    @PostMapping("/guardar")
    public ResponseEntity<EvidenciaTipo> guardarOActualizar(@RequestBody Map<String, Object> request) {
        Long idEvidencia = Long.valueOf(request.get("idEvidencia").toString());
        Long idIndicador = Long.valueOf(request.get("idIndicador").toString());
        String tipo = request.get("tipo").toString();
        Integer orden = Integer.valueOf(request.get("orden").toString());

        EvidenciaTipo resultado = evidenciaTipoService.guardarOActualizar(idEvidencia, idIndicador, tipo, orden);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/obtener/{idEvidencia}")
    public ResponseEntity<EvidenciaTipo> obtenerPorIdEvidencia(@PathVariable Long idEvidencia) {
        return evidenciaTipoService.obtenerPorIdEvidencia(idEvidencia)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/indicador/{idIndicador}")
    public ResponseEntity<List<EvidenciaTipo>> obtenerPorIndicador(@PathVariable Long idIndicador) {
        List<EvidenciaTipo> evidencias = evidenciaTipoService.obtenerPorIndicador(idIndicador);
        return ResponseEntity.ok(evidencias);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<Long, Map<String, Object>>> obtenerMultiples(@RequestBody List<Long> idsEvidencia) {
        Map<Long, Map<String, Object>> resultado = evidenciaTipoService.obtenerMultiples(idsEvidencia);
        return ResponseEntity.ok(resultado);
    }
    
    @PostMapping("/fechas-plazo-final")
    public ResponseEntity<Map<Long, LocalDate>> obtenerFechasPlazoFinalPorIndicadores(@RequestBody List<Long> idsIndicador) {
        log.info("Obteniendo fechas de plazo final para {} indicadores", idsIndicador.size());
        Map<Long, LocalDate> resultado = evidenciaTipoService.obtenerFechasPlazoFinalPorIndicadores(idsIndicador);
        log.info("Fechas de plazo final encontradas: {}", resultado.size());
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/eliminar/{idEvidencia}")
    public ResponseEntity<Void> eliminarPorIdEvidencia(@PathVariable Long idEvidencia) {
        try {
            evidenciaTipoService.eliminarPorIdEvidencia(idEvidencia);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar evidencia tipo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reordenar/{idIndicador}")
    public ResponseEntity<Void> reordenarEvidencias(@PathVariable Long idIndicador) {
        try {
            evidenciaTipoService.reordenarEvidencias(idIndicador);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al reordenar evidencias: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/actualizar-fecha-plazo")
    public ResponseEntity<EvidenciaTipo> actualizarFechaPlazo(@RequestBody Map<String, Object> request) {
        try {
            log.info("Recibiendo petición para actualizar fecha de plazo: {}", request);
            
            if (!request.containsKey("idEvidencia") || !request.containsKey("fechaPlazo")) {
                log.error("Faltan parámetros requeridos");
                return ResponseEntity.badRequest().build();
            }
            
            Long idEvidencia = Long.valueOf(request.get("idEvidencia").toString());
            Long idIndicador = request.containsKey("idIndicador") ? 
                               Long.valueOf(request.get("idIndicador").toString()) : null;
            String fechaStr = request.get("fechaPlazo").toString();
            
            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                log.error("La fecha de plazo está vacía");
                return ResponseEntity.badRequest().build();
            }
            
            LocalDate fechaPlazo = LocalDate.parse(fechaStr);
            log.info("Actualizando fecha de plazo para evidencia {}: {}", idEvidencia, fechaPlazo);
            
            EvidenciaTipo resultado = evidenciaTipoService.actualizarFechaPlazo(idEvidencia, idIndicador, fechaPlazo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al actualizar fecha de plazo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
