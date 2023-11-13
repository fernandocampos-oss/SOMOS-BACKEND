package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.TareaRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;
import pe.gob.essalud.apps.service.TareaService;

@RestController
@RequestMapping(TareaController.TAREA)
@RequiredArgsConstructor
public class TareaController {

    static final String TAREA = "tareas";
    private final TareaService tareaService;

    @PostMapping("/registrar")
    public ResponseEntity<Integer> registrarTarea(@Valid @RequestBody TareaRequestDto dto) {
        int result = tareaService.registrarTarea(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/modificar")
    public int actualizarTareaAdministrador(@RequestParam("nombre") String nombre, @RequestParam("plazo") String plazo, @RequestParam("idTarea") Number idTarea) {
        return tareaService.actualizarTareaAdministrador(nombre, plazo, idTarea);
    }

    @PostMapping("/registrar/evidencia")
    public ResponseEntity<Integer> crearEvidenciaTarea(@Valid @RequestBody EvidenciaRequestDto request) {
        long result = tareaService.crearEvidenciaTarea(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/obtener/evidencia/{idTarea}")
    public ResponseEntity<EvidenciaResponseDto> getEvidenciaByTarea(@PathVariable("idTarea") Integer idTarea) {
        EvidenciaResponseDto evidencia = tareaService.getEvidenciaByTarea(idTarea);
        return new ResponseEntity<EvidenciaResponseDto>(evidencia, HttpStatus.OK);
    }

    @GetMapping("/listar/indicador/{idIndicador}")
    public ResponseEntity<List<Tarea>> getTareasByIdIndicador(@PathVariable("idIndicador") int idIndicador) {
        List<Tarea> lista = tareaService.getTareasByIdIndicador(idIndicador);
        return new ResponseEntity<List<Tarea>>(lista, HttpStatus.OK);
    }

}

