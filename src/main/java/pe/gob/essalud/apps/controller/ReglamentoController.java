package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.miessalud.Reglamento;
import pe.gob.essalud.apps.service.ReglamentoService;

import java.util.List;

@PreAuthorize("authenticated")
@RestController
@RequestMapping(ReglamentoController.REGLAMENTOS)
@RequiredArgsConstructor
public class ReglamentoController {
    static final String REGLAMENTOS = "reglamentos";
    private final ReglamentoService reglamentoService;
    @GetMapping("/consulta/{numDoc}/{semestre}/{anio}")
    public ResponseEntity<Reglamento> getReglamentoBySemestre(@PathVariable String numDoc, @PathVariable int semestre, @PathVariable int anio) {
        Reglamento result = reglamentoService.getReglamentoBySemestre(numDoc, semestre, anio);
        return new ResponseEntity<Reglamento>(result, HttpStatus.OK);
    }
    @GetMapping()
    public ResponseEntity<List<Reglamento>> getAll() {
        List<Reglamento> lista = reglamentoService.getAll();
        return new ResponseEntity<List<Reglamento>>(lista, HttpStatus.OK);
    }
    @PostMapping
    public void save(@RequestBody Reglamento model) {
        reglamentoService.save(model);
    }

}
