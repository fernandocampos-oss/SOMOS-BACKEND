package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoPersonal;
import pe.gob.essalud.apps.service.RequerimientoPersonalService;

import java.net.URI;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(RequerimientoController.REQUERIMIENTOS)
@RequiredArgsConstructor
@Slf4j
public class RequerimientoPersonalController {

    static final String REQUERIMIENTOS = "requerimientosPersonal";
    private final RequerimientoPersonalService requerimientoPersonalService;

    @PostMapping("tareas/registrar/transaccion")
    public ResponseEntity<Object> registrar(@Valid @RequestBody RequerimientoPersonal obj) {
        RequerimientoPersonal reqPer = requerimientoPersonalService.registrar(obj);
        URI location =ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(reqPer.getIdRequerimientoPersonal()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/listar/requerimientos/{idPersonal}/personal")
    public ResponseEntity<List<RequerimientoPersonal>> listarRequerimientosPorPersonal(@PathVariable("idPersonal") Number idPersonal) {
        log.info("idPersonal: [{}]", idPersonal);
        List<RequerimientoPersonal> lista = requerimientoPersonalService.listarRequerimientosPorPersonal(idPersonal);
        Collections.reverse(lista);
        return new ResponseEntity<List<RequerimientoPersonal>>(lista, HttpStatus.OK);
    }

    @GetMapping("/validar/duplicado/requerimiento/{idRequerimiento}/personal/{idPersonal}")
    public List<RequerimientoPersonal> validarDuplicadoRequerimientoPersonal(@PathVariable("idRequerimiento") Number idRequerimiento, @PathVariable("idPersonal") Number idPersonal) {
        log.info("idRequerimiento-idPersonal: [{}-{}]", idRequerimiento, idPersonal);
        return requerimientoPersonalService.validarDuplicadoRequerimientoPersonal(idRequerimiento, idPersonal);
    }
}
