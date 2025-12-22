package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.ValorAlcanzadoPrioridad;
import pe.gob.essalud.apps.service.gdr.ValorAlcanzadoPrioridadService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gdr/valor-alcanzado")
@RequiredArgsConstructor
@Slf4j
public class ValorAlcanzadoPrioridadController {

    private final ValorAlcanzadoPrioridadService valorAlcanzadoPrioridadService;

    @PostMapping("/guardar")
    public ResponseEntity<ValorAlcanzadoPrioridad> guardarOActualizar(@RequestBody Map<String, Object> request) {
        try {
            Long idPrioridad = Long.valueOf(request.get("idPrioridad").toString());
            BigDecimal valorAlcanzado = new BigDecimal(request.get("valorAlcanzado").toString());

            ValorAlcanzadoPrioridad resultado = valorAlcanzadoPrioridadService.guardarOActualizar(idPrioridad, valorAlcanzado);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al guardar valor alcanzado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/obtener/{idPrioridad}")
    public ResponseEntity<ValorAlcanzadoPrioridad> obtenerPorIdPrioridad(@PathVariable Long idPrioridad) {
        return valorAlcanzadoPrioridadService.obtenerPorIdPrioridad(idPrioridad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<Long, BigDecimal>> obtenerMultiples(@RequestBody List<Long> idsPrioridad) {
        Map<Long, BigDecimal> resultado = valorAlcanzadoPrioridadService.obtenerMultiples(idsPrioridad);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/eliminar/{idPrioridad}")
    public ResponseEntity<Void> eliminarPorIdPrioridad(@PathVariable Long idPrioridad) {
        try {
            valorAlcanzadoPrioridadService.eliminarPorIdPrioridad(idPrioridad);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar valor alcanzado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
