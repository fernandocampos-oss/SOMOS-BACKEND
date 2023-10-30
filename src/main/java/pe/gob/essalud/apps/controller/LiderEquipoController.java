package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
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

//    @GetMapping("/listar/votantes")
//    public ResponseEntity<List<Votante>> listAllVotante() {
//        List<Votante> listAllVotante = liderEquipoService.listAllVotante();
//        return new ResponseEntity<List<Votante>>(listAllVotante, HttpStatus.OK);
//    }

//    @GetMapping("/obtener/usuario/{numeroDocumento}")
//    public ResponseEntity<Usuario> findUsuarioByNumeroDocumento(@PathVariable("numeroDocumento") String numeroDocumento) {
//        Usuario usuario = liderEquipoService.findUsuarioByNumeroDocumento(numeroDocumento);
//        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
//    }

}
