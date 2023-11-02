package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaResponseDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaRequestDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
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

    @GetMapping("/obtener/evidencia/{idTarea}")
    public ResponseEntity<EvidenciaResponseDTO> getEvidenciaPorTarea(@PathVariable("idTarea") Integer idTarea) {
        EvidenciaResponseDTO evidencia = tareaService.getEvidenciaPorTarea(idTarea);
        return new ResponseEntity<EvidenciaResponseDTO>(evidencia, HttpStatus.OK);
    }

    @GetMapping("/listar/pois")
    public ResponseEntity<List<Actividad>> listarAllPoi() {
        List<Actividad> lista = tareaService.listarAllPoi();
        return new ResponseEntity<List<Actividad>>(lista, HttpStatus.OK);
    }



}

