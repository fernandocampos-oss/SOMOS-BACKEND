package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.common.util.XlsReport;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.CambiarSegmentoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.CargaMasivaEvaluadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.CargaMasivaEvaluadorResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvaluadorListResponseDto;
import pe.gob.essalud.apps.service.gdr.GestionEvaluadoresService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gdr/evaluadores")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GestionEvaluadoresController {

    private final GestionEvaluadoresService gestionEvaluadoresService;

    /**
     * Listar todos los evaluadores (id_segmento = 3) - PAGINADO
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarEvaluadores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "") String filtro,
            @RequestParam(required = false) Boolean soloConTrabajadores) {
        try {
            log.info("Listando evaluadores - página: {}, tamaño: {}, soloConTrabajadores: {}", page, size, soloConTrabajadores);
            Map<String, Object> resultado = gestionEvaluadoresService.listarEvaluadoresPaginado(page, size, filtro, soloConTrabajadores);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al listar evaluadores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Buscar trabajador por DNI (para agregar como evaluador)
     */
    @GetMapping("/buscar/{dni}")
    public ResponseEntity<?> buscarPorDni(@PathVariable String dni) {
        try {
            log.info("Buscando trabajador por DNI: {}", dni);
            EvaluadorListResponseDto resultado = gestionEvaluadoresService.buscarPorDni(dni);
            if (resultado == null) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "No se encontró trabajador con DNI: " + dni);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al buscar por DNI: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Agregar un evaluador individual
     */
    @PostMapping("/agregar/{dni}")
    public ResponseEntity<?> agregarEvaluador(@PathVariable String dni) {
        try {
            log.info("Agregando evaluador con DNI: {}", dni);
            EvaluadorListResponseDto evaluador = gestionEvaluadoresService.agregarEvaluador(dni);
            return ResponseEntity.ok(evaluador);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("Error al agregar evaluador: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Validar carga masiva de evaluadores (preview)
     */
    @PostMapping("/carga-masiva/validar")
    public ResponseEntity<CargaMasivaEvaluadorResponseDto> validarCargaMasiva(
            @RequestBody CargaMasivaEvaluadorRequestDto request) {
        try {
            log.info("Validando carga masiva de {} DNIs", request.getDnis().size());
            CargaMasivaEvaluadorResponseDto response = gestionEvaluadoresService.validarCargaMasiva(request.getDnis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al validar carga masiva: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Confirmar carga masiva de evaluadores
     */
    @PostMapping("/carga-masiva/confirmar")
    public ResponseEntity<CargaMasivaEvaluadorResponseDto> confirmarCargaMasiva(
            @RequestBody CargaMasivaEvaluadorRequestDto request) {
        try {
            log.info("Confirmando carga masiva de {} DNIs", request.getDnis().size());
            CargaMasivaEvaluadorResponseDto response = gestionEvaluadoresService.confirmarCargaMasiva(request.getDnis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al confirmar carga masiva: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cambiar segmento de un evaluador (quitar rol)
     */
    @PutMapping("/cambiar-segmento")
    public ResponseEntity<?> cambiarSegmento(@RequestBody CambiarSegmentoRequestDto request) {
        try {
            log.info("Cambiando segmento de votante {} a {}", request.getIdVotante(), request.getNuevoSegmento());
            gestionEvaluadoresService.cambiarSegmento(request);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Segmento actualizado correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("Error al cambiar segmento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exportar lista de evaluadores a Excel
     */
    @GetMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            log.info("Exportando evaluadores a Excel");
            List<EvaluadorListResponseDto> evaluadores = gestionEvaluadoresService.listarEvaluadores();

            String sheetName = "Evaluadores GDR";
            String titulo = "Lista de Evaluadores GDR";
            String headers = "DNI|Nombre Completo";
            String fields = "NumeroDocumento|NombreCompleto";

            byte[] excelBytes = XlsReport.getReporte(sheetName, headers, titulo, fields, evaluadores, EvaluadorListResponseDto.class);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            responseHeaders.setContentDispositionFormData("attachment", "evaluadores_gdr.xlsx");
            responseHeaders.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar evaluadores a Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
