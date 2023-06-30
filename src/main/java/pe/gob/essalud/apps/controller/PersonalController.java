package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalFiltroNombreDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Personal;
import pe.gob.essalud.apps.service.PersonalService;

@RestController
@RequestMapping(PersonalController.PERSONAL)
@RequiredArgsConstructor
@Slf4j
public class PersonalController {

    static final String PERSONAL = "personal";
    private final PersonalService personalService;

    @GetMapping("/listar/dependencia/{idDependencia}/estadoAsignado/{estadoAsignado}")
    public ResponseEntity<List<Personal>> listarPersonalPorDependenciaAsignado(@PathVariable("idDependencia") Number idDependencia, @PathVariable("estadoAsignado") Character estadoAsignado) {
        log.info("personal: [{}-{}]", idDependencia, estadoAsignado);
        List<Personal> lista = personalService.listarPersonalPorDependenciaAsignado(idDependencia, estadoAsignado);
        Collections.reverse(lista);
        return new ResponseEntity<List<Personal>>(lista, HttpStatus.OK);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrar(@Valid @RequestBody Personal persona) {
        log.info("personal: {}", persona);
        if (persona != null) {
            persona.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        Personal paciente = personalService.registrar(persona);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(paciente.getIdPersonal()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/filtrarPersonal")
    public ResponseEntity<List<Personal>> buscarPersonalPorNombre(@RequestBody PersonalFiltroNombreDTO filtro) {
        List<Personal> personas = new ArrayList<>();

        if (filtro != null && filtro.getNombres() != null) {
            personas = personalService.buscarPersonalPorNombre(filtro);
        }
        return new ResponseEntity<List<Personal>>(personas, HttpStatus.OK);
    }


}
