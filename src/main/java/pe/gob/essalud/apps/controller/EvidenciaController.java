package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadExistRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaSustentoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorExistRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;
import pe.gob.essalud.apps.service.EvidenciaService;

@RestController
@RequestMapping(EvidenciaController.TAREA)
@RequiredArgsConstructor
public class EvidenciaController {

    static final String TAREA = "evidencias";
    private final EvidenciaService evidenciaService;

    @PostMapping("/registrar/exist-indicador")
    public void registrarEvidenciaExistIndicador(@Valid @RequestBody IndicadorExistRequestDto dto) {
        evidenciaService.registrarEvidenciaExistIndicador(dto);
    }

    @PostMapping("/registrar/exist-prioridad")
    public void registrarIndicadorExistPrioridad(@Valid @RequestBody PrioridadExistRequestDto dto) {
        evidenciaService.registrarIndicadorExistPrioridad(dto);
    }

    @GetMapping("/modificar")
    public int actualizarTareaAdministrador(@RequestParam("nombre") String nombre, @RequestParam("plazo") String plazo, @RequestParam("idEvidencia") Number idEvidencia) {
        return evidenciaService.actualizarTareaAdministrador(nombre, plazo, idEvidencia);
    }

    @PostMapping("/registrar/sustento")
    public ResponseEntity<Integer> crearSustentoEvidencia(@Valid @RequestBody EvidenciaSustentoRequestDto request) {
        long result = evidenciaService.crearSustentoEvidencia(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/obtener/evidencia/{idEvidencia}")
    public ResponseEntity<EvidenciaResponseDto> getEvidenciaByTarea(@PathVariable("idEvidencia") Integer idEvidencia) {
        EvidenciaResponseDto evidencia = evidenciaService.getEvidenciaByTarea(idEvidencia);
        return new ResponseEntity<EvidenciaResponseDto>(evidencia, HttpStatus.OK);
    }

    @GetMapping("/listar/indicador/{idIndicador}")
    public ResponseEntity<List<Evidencia>> listEvidenciaByIdIndicador(@PathVariable("idIndicador") int idIndicador) {
        List<Evidencia> lista = evidenciaService.listEvidenciaByIdIndicador(idIndicador);
        return new ResponseEntity<List<Evidencia>>(lista, HttpStatus.OK);
    }

////    @GetMapping("/aprobar")
////    public int aprobarIndicador(@RequestParam("estado") Number estado, @RequestParam("idIndicadorUsuario") Number idIndicadorUsuario) {
////        return indicadorUsuarioService.aprobarIndicador(estado, idIndicadorUsuario);
////    }
//
////    @GetMapping("/rechazar")
////    public int rechazarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
////        return requerimientoUsuarioService.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
////    }

}

