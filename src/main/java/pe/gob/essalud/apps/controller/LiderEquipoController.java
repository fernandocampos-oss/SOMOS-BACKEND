package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.LiderEquipo;
import pe.gob.essalud.apps.service.LiderEquipoService;

import java.util.List;

@RestController
@RequestMapping(LiderEquipoController.LIDER_EQUIPO)
@RequiredArgsConstructor
public class LiderEquipoController {

    static final String LIDER_EQUIPO = "lideres-equipos";
    private final LiderEquipoService liderEquipoService;

    @PostMapping("/integrante/agregar")
    public Integer save(@RequestBody LiderEquipo liderEquipo) {
        return liderEquipoService.save(liderEquipo);
    }

    @GetMapping("/listar/equipo")
    public ResponseEntity<List<LiderEquipo>> listarIntegrantesPorLider() {
        List<LiderEquipo> lista = liderEquipoService.listarIntegrantesPorLider();
        return new ResponseEntity<List<LiderEquipo>>(lista, HttpStatus.OK);
    }

    @GetMapping("/integrante/eliminar")
    public int eliminarIntegrante(@RequestParam("idIntegrante") Number idIntegrante) {
        return liderEquipoService.eliminarIntegrante(idIntegrante);
    }

}
