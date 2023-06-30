package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaValidacionDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaValidacionTransaccionalDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;
import pe.gob.essalud.apps.service.TareaService;

@RestController
@RequestMapping(TareaController.TAREA)
@RequiredArgsConstructor
@Slf4j
public class TareaController {

    static final String TAREA = "tareas";
    private final TareaService tareaService;

    @GetMapping
    public ResponseEntity<List<Tarea>> listar() {
        List<Tarea> lista = tareaService.listar();
        return new ResponseEntity<List<Tarea>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/requerimientoPersonal/{idRequerimientoPersonal}")
    public ResponseEntity<List<Tarea>> listarTareaPorRequermientoPersonal(@PathVariable("idRequerimientoPersonal") Number idRequerimientoPersonal) {
        log.info("id_requerimiento_personal: [{}]", idRequerimientoPersonal);
        List<Tarea> lista = tareaService.listarTareaPorRequermientoPersonal(idRequerimientoPersonal);
        return new ResponseEntity<List<Tarea>>(lista, HttpStatus.OK);
    }

    @PostMapping("/registrar/noDuplicado")
    public ResponseEntity<Integer> registrarTareaNoDuplicado(@Valid @RequestBody TareaValidacionTransaccionalDTO obj) {
        int dto = tareaService.registrarTareaNoDuplicado(obj);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto).toUri();
        return ResponseEntity.created(location).build();
    }

}

