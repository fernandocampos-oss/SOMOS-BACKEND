package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Archivo;
import pe.gob.essalud.apps.service.ArchivoService;
import pe.gob.essalud.apps.service.EvidenciaService;

import javax.validation.Valid;

@RestController
@RequestMapping(ArchivoController.ARCHIVO)
@RequiredArgsConstructor
public class ArchivoController {

    static final String ARCHIVO = "archivos";
    private final ArchivoService archivoService;

    @PostMapping("registrar")
    public ResponseEntity<Object> registrar(@Valid @RequestBody Archivo obj) {
        Archivo archivo = archivoService.registrar(obj);

        return new ResponseEntity<>(archivo, HttpStatus.CREATED);
    }
}
