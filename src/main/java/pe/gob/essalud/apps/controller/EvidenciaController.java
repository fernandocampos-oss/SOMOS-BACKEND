package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;
import pe.gob.essalud.apps.service.EvidenciaService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping(EvidenciaController.EVIDENCIA)
@RequiredArgsConstructor
public class EvidenciaController {

    static final String EVIDENCIA = "evidencias";
    private final EvidenciaService evidenciaService;

    @PostMapping("registrar")
    public ResponseEntity<Object> registrar(@Valid @RequestBody Evidencia obj) {
        if (obj != null) {
            obj.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        Evidencia evidencia = evidenciaService.registrar(obj);

        return new ResponseEntity<Object>(evidencia, HttpStatus.CREATED);
    }
}
