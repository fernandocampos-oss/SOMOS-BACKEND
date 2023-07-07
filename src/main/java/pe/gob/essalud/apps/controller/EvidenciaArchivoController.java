package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.EvidenciaArchivo;
import pe.gob.essalud.apps.service.EvidenciaArchivoService;
import pe.gob.essalud.apps.service.EvidenciaService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping(EvidenciaArchivoController.EVIDENCIA_ARCHIVO)
@RequiredArgsConstructor
public class EvidenciaArchivoController {

    static final String EVIDENCIA_ARCHIVO = "evidenciasArchivos";
    private final EvidenciaArchivoService evidenciaArchivoService;

    @PostMapping("registrar")
    public ResponseEntity<Object> registrar(@Valid @RequestBody EvidenciaArchivo obj) {
        if (obj != null) {
            obj.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        EvidenciaArchivo evidenciaArchivo = evidenciaArchivoService.registrar(obj);

        return new ResponseEntity<Object>(evidenciaArchivo, HttpStatus.CREATED);
    }

    @GetMapping("/listar/{idEvidenciaArchivo}/estado")
    public ResponseEntity<EvidenciaArchivo> listarArchivoPorEstadoActivo(@PathVariable("idEvidenciaArchivo") Number idEvidenciaArchivo) {
        EvidenciaArchivo lista = evidenciaArchivoService.listarArchivoPorEstadoActivo(idEvidenciaArchivo);
        return new ResponseEntity<EvidenciaArchivo>(lista, HttpStatus.OK);
    }

    @PostMapping("/eliminar/estado/{estado}/id/{idEvidenciaArchivo}")
    public int eliminarArchivo(@PathVariable("estado") Boolean estado, @PathVariable("idEvidenciaArchivo") Number idEvidenciaArchivo) {
        return evidenciaArchivoService.eliminarArchivo(estado, idEvidenciaArchivo);
    }

}
