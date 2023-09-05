package pe.gob.essalud.apps.controller;

import java.net.URI;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;
import pe.gob.essalud.apps.service.TareaService;

@RestController
@RequestMapping(TareaController.TAREA)
@RequiredArgsConstructor
@Slf4j
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

//    @GetMapping("/listar/requerimientoPersonal/{idRequerimientoPersonal}")
//    public ResponseEntity<List<Tarea>> listarTareaPorRequermientoPersonal(@PathVariable("idRequerimientoPersonal") Number idRequerimientoPersonal) {
//        log.info("id_requerimiento_personal: [{}]", idRequerimientoPersonal);
//        List<Tarea> lista = tareaService.listarTareaPorRequermientoPersonal(idRequerimientoPersonal);
//        return new ResponseEntity<List<Tarea>>(lista, HttpStatus.OK);
//    }
//
//    @GetMapping("/listar/personal/{idPersonal}")
//    public ResponseEntity<List<Tarea>> listarTareaPorPersonal(@PathVariable("idPersonal") Number idPersonal) {
//        log.info("id_personal: [{}]", idPersonal);
//        List<Tarea> lista = tareaService.listarTareaPorPersonal(idPersonal);
//        return new ResponseEntity<List<Tarea>>(lista, HttpStatus.OK);
//    }

}

