package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.ResultadosFinales;
import pe.gob.essalud.apps.service.gdr.ResultadosFinalesService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/gdr/resultados-finales")
@RequiredArgsConstructor
@Slf4j
public class ResultadosFinalesController {

    private final ResultadosFinalesService resultadosFinalesService;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarOActualizar(@RequestBody Map<String, Object> request) {
        try {
            Long idVotante = Long.parseLong(request.get("idVotante").toString());
            Integer anio = Integer.parseInt(request.get("anio").toString());
            String rendimientoDistinguido = request.get("rendimientoDistinguido") != null 
                ? request.get("rendimientoDistinguido").toString() : null;
            String accionesCapacitacion = request.get("accionesCapacitacion") != null 
                ? request.get("accionesCapacitacion").toString() : null;
            String otrasAcciones = request.get("otrasAcciones") != null 
                ? request.get("otrasAcciones").toString() : null;
            
            LocalDate fechaReunion = null;
            if (request.get("fechaReunion") != null && !request.get("fechaReunion").toString().isEmpty()) {
                fechaReunion = LocalDate.parse(request.get("fechaReunion").toString());
            }
            
            String permanenciaSeisMeses = request.get("permanenciaSeisMeses") != null 
                ? request.get("permanenciaSeisMeses").toString() : "SI";

            ResultadosFinales resultado = resultadosFinalesService.guardarOActualizar(
                idVotante, anio, rendimientoDistinguido, accionesCapacitacion, otrasAcciones, fechaReunion, permanenciaSeisMeses
            );

            log.info("Resultados finales guardados exitosamente para votante {} año {}", idVotante, anio);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al guardar resultados finales: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage() != null ? e.getMessage() : "Error desconocido"
            ));
        }
    }

    @GetMapping("/obtener/{idVotante}/{anio}")
    public ResponseEntity<ResultadosFinales> obtener(@PathVariable Long idVotante, @PathVariable Integer anio) {
        Optional<ResultadosFinales> resultado = resultadosFinalesService.obtenerPorVotanteYAnio(idVotante, anio);
        return resultado.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<Long, ResultadosFinales>> obtenerMultiples(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> idsVotantesRaw = (List<Object>) request.get("idsVotantes");
            Integer anio = Integer.parseInt(request.get("anio").toString());

            // Convertir a List<Long> manejando diferentes tipos
            List<Long> idsVotantes = new java.util.ArrayList<>();
            for (Object id : idsVotantesRaw) {
                if (id instanceof Integer) {
                    idsVotantes.add(((Integer) id).longValue());
                } else if (id instanceof Long) {
                    idsVotantes.add((Long) id);
                } else {
                    idsVotantes.add(Long.parseLong(id.toString()));
                }
            }

            log.info("Buscando resultados finales para {} votantes en año {}", idsVotantes.size(), anio);
            Map<Long, ResultadosFinales> resultados = resultadosFinalesService.obtenerMultiples(idsVotantes, anio);
            log.info("Encontrados {} resultados", resultados.size());
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            log.error("Error al obtener resultados finales múltiples: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/eliminar/{idVotante}/{anio}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idVotante, @PathVariable Integer anio) {
        try {
            resultadosFinalesService.eliminar(idVotante, anio);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar resultados finales: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
