package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.CargaMasivaVotanteDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdateEvidenciaDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdateVotanteDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.TrabajadorResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.VotantePlanillaResponseDto;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;
import pe.gob.essalud.apps.service.EquipoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/listar/evaluador")
    public ResponseEntity<Map<String, String>> getListEvaluadorByIdUsuarioJefe() {
        String votante = equipoService.getListEvaluadorByIdUsuarioJefe();
        Map<String, String> response = new HashMap<>();
        response.put("evaluador", votante);
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    @GetMapping("/buscar/nombre")
    public List<Votante> findVotanteByNombre(@RequestParam("nombre") String nombre) {
        return equipoService.findVotanteByNombre(nombre);
    }
    


    @GetMapping("/buscar/nombre2")
    public List<VotantePlanillaResponseDto> findVotanteByNombre2(@RequestParam("nombre") String nombre) {
        return equipoService.findVotanteByNombre2(nombre);
    }

    @GetMapping("/listar/perfil/votantes")
    public List<Votante> findAllVotantePerfil() {
        return equipoService.findAllVotantePerfil();
    }

    @PutMapping("/modificar/votante/{id}")
    public void modificarPerfilVotante(@PathVariable int id, @RequestBody UpdateVotanteDto request) {
        equipoService.modificarPerfilVotante(id, request);
    }

    @PostMapping("/carga/excel/votante")
    public List<CargaMasivaVotanteDto> cargaMasivaVotante(@RequestBody List<CargaMasivaVotanteDto> listVotantes) {
        return equipoService.cargaMasivaVotante(listVotantes);
    }

    @PostMapping("/registrar/evaluador")
    public void registrarEvaluador(@RequestBody Equipo equipo) {
        equipoService.registrarEvaluador(equipo);
    }

}
