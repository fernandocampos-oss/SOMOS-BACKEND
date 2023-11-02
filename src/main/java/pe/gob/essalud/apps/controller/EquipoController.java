package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;
import pe.gob.essalud.apps.service.EquipoService;

import java.util.List;

@RestController
@RequestMapping(EquipoController.EQUIPO)
@RequiredArgsConstructor
public class EquipoController {
    static final String EQUIPO = "equipos";

    private final EquipoService equipoService;

    @PostMapping("/registrar")
    public void registrarTrabajador(@RequestBody Equipo equipo) {
        equipoService.registrarTrabajador(equipo);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Equipo>> getListTrabajadoresByIdUsuarioLider() {
        List<Equipo> lista = equipoService.getListTrabajadoresByIdUsuarioJefe();
        return new ResponseEntity<List<Equipo>>(lista, HttpStatus.OK);
    }

    @GetMapping("/eliminar")
    public int eliminarTrabajador(@RequestParam("idEquipo") Number idEquipo) {
        return equipoService.eliminarTrabajador(idEquipo);
    }

    @GetMapping("/listar/votantes")
    public ResponseEntity<List<PersonalDTO>> listAllVotante() {
        List<PersonalDTO> listAllVotante = equipoService.listAllVotante();
        return new ResponseEntity<List<PersonalDTO>>(listAllVotante, HttpStatus.OK);
    }

//    @GetMapping("/obtener/usuario/{numeroDocumento}")
//    public ResponseEntity<Usuario> findUsuarioByNumeroDocumento(@PathVariable("numeroDocumento") String numeroDocumento) {
//        Usuario usuario = liderEquipoService.findUsuarioByNumeroDocumento(numeroDocumento);
//        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
//    }

}
