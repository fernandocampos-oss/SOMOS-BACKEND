package pe.gob.essalud.apps.controller.gdr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.SentidoIndicador;
import pe.gob.essalud.apps.service.gdr.SentidoIndicadorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/gdr/sentido-indicador")
@CrossOrigin(origins = "*")
public class SentidoIndicadorController {

    @Autowired
    private SentidoIndicadorService sentidoIndicadorService;

    // Obtener sentido por ID de indicador
    @GetMapping("/{idIndicador}")
    public ResponseEntity<?> obtenerPorIdIndicador(@PathVariable Long idIndicador) {
        Optional<SentidoIndicador> sentido = sentidoIndicadorService.obtenerPorIdIndicador(idIndicador);
        if (sentido.isPresent()) {
            return ResponseEntity.ok(sentido.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró sentido para el indicador: " + idIndicador);
        }
    }

    // Obtener sentidos para múltiples indicadores
    @PostMapping("/batch")
    public ResponseEntity<Map<Long, String>> obtenerSentidosPorIndicadores(@RequestBody List<Long> idIndicadores) {
        log.info("=== /batch: Recibidos {} IDs de indicadores: {}", idIndicadores.size(), idIndicadores);
        Map<Long, String> sentidos = sentidoIndicadorService.obtenerSentidosPorIndicadores(idIndicadores);
        log.info("=== /batch: Retornando {} sentidos: {}", sentidos.size(), sentidos);
        return ResponseEntity.ok(sentidos);
    }

    // Guardar o actualizar sentido
    @PostMapping
    public ResponseEntity<SentidoIndicador> guardarOActualizar(@RequestBody Map<String, Object> request) {
        Long idIndicador = Long.valueOf(request.get("idIndicador").toString());
        String sentido = request.get("sentido").toString();
        
        SentidoIndicador resultado = sentidoIndicadorService.guardarOActualizar(idIndicador, sentido);
        return ResponseEntity.ok(resultado);
    }

    // Actualizar sentido
    @PutMapping("/{idIndicador}")
    public ResponseEntity<SentidoIndicador> actualizar(
            @PathVariable Long idIndicador,
            @RequestBody Map<String, String> request) {
        String sentido = request.get("sentido");
        SentidoIndicador resultado = sentidoIndicadorService.guardarOActualizar(idIndicador, sentido);
        return ResponseEntity.ok(resultado);
    }

    // Eliminar sentido
    @DeleteMapping("/{idIndicador}")
    public ResponseEntity<?> eliminar(@PathVariable Long idIndicador) {
        sentidoIndicadorService.eliminarPorIdIndicador(idIndicador);
        return ResponseEntity.ok("Sentido eliminado correctamente");
    }

    // Obtener todos (para diagnóstico)
    @GetMapping
    public ResponseEntity<List<SentidoIndicador>> obtenerTodos() {
        log.info("=== Obteniendo todos los sentidos de la BD ===");
        List<SentidoIndicador> sentidos = sentidoIndicadorService.obtenerTodos();
        log.info("Total de sentidos en BD: {}", sentidos.size());
        for (SentidoIndicador s : sentidos) {
            log.info("  - ID: {}, idIndicador: {}, sentido: {}", s.getId(), s.getIdIndicador(), s.getSentido());
        }
        return ResponseEntity.ok(sentidos);
    }
    
    // Endpoint de prueba para insertar manualmente (GET para probar desde navegador)
    @GetMapping("/test-insert/{idIndicador}/{sentido}")
    public ResponseEntity<?> testInsert(@PathVariable Long idIndicador, @PathVariable String sentido) {
        log.info("=== TEST INSERT: idIndicador={}, sentido={} ===", idIndicador, sentido);
        try {
            SentidoIndicador resultado = sentidoIndicadorService.guardarOActualizar(idIndicador, sentido);
            log.info("Resultado exitoso: id={}, idIndicador={}, sentido={}", 
                resultado.getId(), resultado.getIdIndicador(), resultado.getSentido());
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error completo en test insert:", e);
            
            // Obtener causa raíz
            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }
            
            String mensajeError = "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage() 
                + " | Causa raíz: " + causa.getClass().getSimpleName() + " - " + causa.getMessage();
            
            return ResponseEntity.status(500).body(mensajeError);
        }
    }
}
