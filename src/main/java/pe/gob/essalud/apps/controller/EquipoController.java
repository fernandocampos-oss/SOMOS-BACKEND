package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.TrabajadorResponseDto;
import pe.gob.essalud.apps.model.miessalud.Votante;
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
    public ResponseEntity<List<Equipo>> getListTrabajadoresByIdUsuarioJefe() {
        List<Equipo> lista = equipoService.getListTrabajadoresByIdUsuarioJefe();
        return new ResponseEntity<List<Equipo>>(lista, HttpStatus.OK);
    }

    @GetMapping("/eliminar")
    public int eliminarTrabajador(@RequestParam("idEquipo") Number idEquipo) {
        return equipoService.eliminarTrabajador(idEquipo);
    }

    @GetMapping("/listar/votantes")
    public ResponseEntity<List<TrabajadorResponseDto>> listAllVotante() {
        List<TrabajadorResponseDto> listAllVotante = equipoService.listAllVotante();
        return new ResponseEntity<List<TrabajadorResponseDto>>(listAllVotante, HttpStatus.OK);
    }

    @GetMapping("/obtener/votante/segmento")
    public ResponseEntity<Votante> getVotanteByIdUsuario() {
        Votante usuario = equipoService.getVotanteByIdUsuario();
        return new ResponseEntity<Votante>(usuario, HttpStatus.OK);
    }

}
