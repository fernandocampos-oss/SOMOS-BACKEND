package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.EvidenciaArchivo;
import pe.gob.essalud.apps.service.EvidenciaArchivoService;
import pe.gob.essalud.apps.service.EvidenciaService;

import javax.validation.Valid;

@RestController
@RequestMapping(EvidenciaArchivoController.EVIDENCIA_ARCHIVO)
@RequiredArgsConstructor
public class EvidenciaArchivoController {

    static final String EVIDENCIA_ARCHIVO = "evidenciasArchivos";
    private final EvidenciaArchivoService evidenciaArchivoService;

    @PostMapping("registrar")
    public ResponseEntity<Object> registrar(@Valid @RequestBody EvidenciaArchivo obj) {
        EvidenciaArchivo evidenciaArchivo = evidenciaArchivoService.registrar(obj);

        return new ResponseEntity<Object>(evidenciaArchivo, HttpStatus.CREATED);
    }

}
