package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaResponseDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaRequestDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Poi;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;
import pe.gob.essalud.apps.service.TareaService;

@RestController
@RequestMapping(TareaController.TAREA)
@RequiredArgsConstructor
public class TareaController {

    static final String TAREA = "tareas";
    private final TareaService tareaService;

    @PostMapping("/registrar")
    public ResponseEntity<Integer> registrarTarea(@Valid @RequestBody TareaDTO dto) {
        int result = tareaService.registrarTarea(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/modificar")
    public int actualizarTareaAdministrador(@RequestParam("nombreTarea") String nombreTarea, @RequestParam("plazo") String plazo, @RequestParam("idTarea") Number idTarea) {
        return tareaService.actualizarTareaAdministrador(nombreTarea, plazo, idTarea);
    }

    @PostMapping("/registrar/evidencia")
    public ResponseEntity<Integer> crearEvidencia(@Valid @RequestBody EvidenciaRequestDTO request) {
        long result = tareaService.crearEvidencia(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/listar/evidencias/{idTarea}")
    public ResponseEntity<List<EvidenciaResponseDTO>> listarEvidenciaTarea(@PathVariable("idTarea") Integer idTarea) {
        List<EvidenciaResponseDTO> lista = tareaService.listarEvidenciaTarea(idTarea);
        return new ResponseEntity<List<EvidenciaResponseDTO>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/pois")
    public ResponseEntity<List<Poi>> listarAllPoi() {
        List<Poi> lista = tareaService.listarAllPoi();
        return new ResponseEntity<List<Poi>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/tipoIngreso")
    public ResponseEntity<List<TipoIngreso>> listarAllTipoIngreso() {
        List<TipoIngreso> lista = tareaService.listarAllTipoIngreso();
        return new ResponseEntity<List<TipoIngreso>>(lista, HttpStatus.OK);
    }

}

