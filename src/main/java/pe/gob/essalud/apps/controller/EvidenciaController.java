package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;
import pe.gob.essalud.apps.service.EvidenciaService;
import pe.gob.essalud.apps.service.gdr.SentidoIndicadorService;
import pe.gob.essalud.apps.service.gdr.EvidenciaTipoService;
import pe.gob.essalud.apps.service.gdr.StorageService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(EvidenciaController.TAREA)
@RequiredArgsConstructor
public class EvidenciaController {

    static final String TAREA = "evidencias";
    private final EvidenciaService evidenciaService;
    private final SentidoIndicadorService sentidoIndicadorService;
    private final EvidenciaTipoService evidenciaTipoService;
    private final StorageService storageService;

    @PostMapping("/registrar/exist-indicador")
    public void registrarEvidenciaExistIndicador(@Valid @RequestBody IndicadorExistRequestDto dto) {
        evidenciaService.registrarEvidenciaExistIndicador(dto);
    }

    @PostMapping("/registrar/exist-prioridad")
    public void registrarIndicadorExistPrioridad(@Valid @RequestBody PrioridadExistRequestDto dto) {
        // Guardar indicador y obtener el ID
        Integer idIndicador = evidenciaService.registrarIndicadorExistPrioridad(dto);
        log.info("Indicador en prioridad existente registrado con ID: {}", idIndicador);
        
        // Guardar sentido del indicador en transacción separada
        if (dto.getSentidoIndicador() != null && !dto.getSentidoIndicador().isEmpty()) {
            try {
                log.info("Guardando sentido del indicador: {} para ID: {}", dto.getSentidoIndicador(), idIndicador);
                sentidoIndicadorService.guardarOActualizar(idIndicador.longValue(), dto.getSentidoIndicador());
                log.info("Sentido guardado exitosamente");
            } catch (Exception e) {
                log.error("Error al guardar sentido: {}", e.getMessage(), e);
            }
        }
        
        // Guardar fecha de plazo final en transacción separada
        if (dto.getFechaPlazoFinal() != null && !dto.getFechaPlazoFinal().isEmpty()) {
            try {
                LocalDate fechaPlazo = LocalDate.parse(dto.getFechaPlazoFinal().substring(0, 10));
                log.info("Guardando fecha plazo final: {} para ID: {}", fechaPlazo, idIndicador);
                evidenciaTipoService.guardarFechaPlazoFinalPorIndicador(idIndicador.longValue(), fechaPlazo);
                log.info("Fecha plazo guardada exitosamente");
            } catch (Exception e) {
                log.error("Error al guardar fecha plazo: {}", e.getMessage(), e);
            }
        }
    }

    @PostMapping("/registrar/sustento")
    public ResponseEntity<Integer> crearSustentoEvidencia(@Valid @RequestBody EvidenciaSustentoRequestDto request) {
        long result = evidenciaService.crearSustentoEvidencia(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/obtener/evidencia/{idEvidencia}")
    public ResponseEntity<EvidenciaResponseDto> getEvidenciaById(@PathVariable("idEvidencia") Integer idEvidencia) {
        EvidenciaResponseDto evidencia = evidenciaService.getEvidenciaById(idEvidencia);
        return new ResponseEntity<EvidenciaResponseDto>(evidencia, HttpStatus.OK);
    }

//    @GetMapping("/listar/indicador/{idIndicador}")
//    public ResponseEntity<List<Evidencia>> listEvidenciaByIdIndicador(@PathVariable("idIndicador") int idIndicador) {
//        List<Evidencia> lista = evidenciaService.listEvidenciaByIdIndicador(idIndicador);
//        return new ResponseEntity<List<Evidencia>>(lista, HttpStatus.OK);
//    }

    @PutMapping("/modificar/{id}")
    public void modificarEvidencia(@PathVariable int id, @RequestBody UpdateEvidenciaDto request) {
        evidenciaService.modificarEvidencia(id, request);
    }

    @PutMapping("/modificar/calificacion")
    public void modificarCalificacion(@RequestBody ApruebaEvidenciaRequestDto request){
        evidenciaService.aprobarEvidencia(request);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminarEvidencia(@PathVariable int id) {
        evidenciaService.eliminarEvidencia(id);
    }

    @DeleteMapping("/eliminar/sustento/{id}")
    public void eliminarSustento(@PathVariable int id) {
        evidenciaService.eliminarSustento(id);
    }

    @GetMapping("/storage/health")
    public ResponseEntity<Map<String, String>> storageHealth() {
        boolean disponible = storageService.healthCheck();
        Map<String, String> result = new HashMap<>();
        result.put("status", disponible ? "ok" : "error");
        result.put("fileServer", disponible ? "conectado" : "no disponible");
        HttpStatus httpStatus = disponible ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(result, httpStatus);
    }
}

