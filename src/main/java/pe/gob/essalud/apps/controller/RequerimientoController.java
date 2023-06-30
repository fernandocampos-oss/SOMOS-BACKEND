package pe.gob.essalud.apps.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;
import pe.gob.essalud.apps.service.RequerimientoService;

@RestController
@RequestMapping(RequerimientoController.REQUERIMIENTOS)
@RequiredArgsConstructor
@Slf4j
public class RequerimientoController {

    static final String REQUERIMIENTOS = "requerimientos";
    private final RequerimientoService requerimientoService;

    @GetMapping("/listar")
    public ResponseEntity<List<Requerimiento>> listar() {
        List<Requerimiento> lista = requerimientoService.listar();
        Collections.reverse(lista);
        return new ResponseEntity<List<Requerimiento>>(lista, HttpStatus.OK);
    }

    @GetMapping("/aprobar")
    public int aprobarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("idRequerimiento") Number idRequerimiento) {
        log.info("estado-idRequerimiento: [{}-{}]", estado, idRequerimiento);
        return requerimientoService.aprobarRequerimiento(estado, idRequerimiento);
    }

    @GetMapping("/rechazar")
    public int rechazarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("idRequerimiento") Number idRequerimiento) {
        log.info("estado-idRequerimiento-motivo: [{}-{}-{}]", estado, motivo, idRequerimiento);
        return requerimientoService.rechazarRequerimiento(estado, motivo, idRequerimiento);
    }

    @GetMapping("/derivar")
    public int derivarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("idAreaReceptor") Number idAreaReceptor, @RequestParam("idRequerimiento") Number idRequerimiento) {
        log.info("estado-idRequerimiento-motivo-gerenciareceptor: [{}-{}-{}-{}]", estado, motivo, idAreaReceptor, idRequerimiento);
        return requerimientoService.derivarRequerimiento(estado, motivo, idAreaReceptor, idRequerimiento);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrar(@Valid @RequestBody Requerimiento req) {
        if (req != null) {
            req.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        Requerimiento requerimiento = requerimientoService.registrar(req);

        URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(requerimiento.getIdRequerimiento()).toUri();
        return ResponseEntity.created(location).build();
    }

}