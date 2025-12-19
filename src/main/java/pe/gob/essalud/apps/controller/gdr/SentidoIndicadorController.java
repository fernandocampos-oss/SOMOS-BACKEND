package pe.gob.essalud.apps.controller.gdr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.SentidoIndicador;
import pe.gob.essalud.apps.service.gdr.SentidoIndicadorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Map<Long, String> sentidos = sentidoIndicadorService.obtenerSentidosPorIndicadores(idIndicadores);
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

    // Obtener todos
    @GetMapping
    public ResponseEntity<List<SentidoIndicador>> obtenerTodos() {
        List<SentidoIndicador> sentidos = sentidoIndicadorService.obtenerTodos();
        return ResponseEntity.ok(sentidos);
    }
}
