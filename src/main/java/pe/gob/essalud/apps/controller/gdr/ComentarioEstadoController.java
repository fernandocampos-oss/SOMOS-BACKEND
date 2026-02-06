package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.gdr.ComentarioEstado;
import pe.gob.essalud.apps.service.gdr.ComentarioEstadoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gdr/comentario-estado")
@RequiredArgsConstructor
@Slf4j
public class ComentarioEstadoController {

    private final ComentarioEstadoService comentarioEstadoService;

    @PostMapping("/guardar")
    public ResponseEntity<ComentarioEstado> guardarOActualizar(@RequestBody Map<String, Object> request) {
        Long idEvidencia = Long.valueOf(request.get("idEvidencia").toString());
        String tipoComentario = request.get("tipoComentario") != null ? request.get("tipoComentario").toString() : "individual";
        String estadoDropdown = request.get("estadoDropdown") != null ? request.get("estadoDropdown").toString() : null;
        String comentarioAdicional = request.get("comentarioAdicional") != null ? request.get("comentarioAdicional").toString() : null;

        ComentarioEstado resultado = comentarioEstadoService.guardarOActualizar(idEvidencia, tipoComentario, estadoDropdown, comentarioAdicional);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/obtener/{idEvidencia}")
    public ResponseEntity<ComentarioEstado> obtenerPorIdEvidencia(@PathVariable Long idEvidencia) {
        return comentarioEstadoService.obtenerPorIdEvidencia(idEvidencia)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/obtener/{idEvidencia}/{tipoComentario}")
    public ResponseEntity<ComentarioEstado> obtenerPorIdEvidenciaYTipo(@PathVariable Long idEvidencia, @PathVariable String tipoComentario) {
        return comentarioEstadoService.obtenerPorIdEvidenciaYTipo(idEvidencia, tipoComentario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<Long, Map<String, Object>>> obtenerMultiples(@RequestBody List<Long> idsEvidencia) {
        Map<Long, Map<String, Object>> resultado = comentarioEstadoService.obtenerMultiples(idsEvidencia);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/batch-all")
    public ResponseEntity<Map<String, Map<String, Object>>> obtenerTodosMultiples(@RequestBody List<Long> idsEvidencia) {
        Map<String, Map<String, Object>> resultado = comentarioEstadoService.obtenerTodosMultiples(idsEvidencia);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/batch-por-tipo")
    public ResponseEntity<Map<Long, Map<String, Object>>> obtenerMultiplesPorTipo(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> idsEvidencia = ((List<Number>) request.get("idsEvidencia")).stream()
            .map(Number::longValue)
            .collect(java.util.stream.Collectors.toList());
        String tipoComentario = request.get("tipoComentario") != null ? request.get("tipoComentario").toString() : "individual";
        
        Map<Long, Map<String, Object>> resultado = comentarioEstadoService.obtenerMultiplesPorTipo(idsEvidencia, tipoComentario);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/eliminar/{idEvidencia}")
    public ResponseEntity<Void> eliminarPorIdEvidencia(@PathVariable Long idEvidencia) {
        try {
            comentarioEstadoService.eliminarPorIdEvidencia(idEvidencia);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar comentario estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
