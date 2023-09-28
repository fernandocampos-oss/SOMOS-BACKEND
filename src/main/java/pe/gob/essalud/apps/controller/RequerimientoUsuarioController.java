package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.dto.publicacion.request.PublicacionRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.UnidadOrganizativa;
import pe.gob.essalud.apps.service.RequerimientoService;
import pe.gob.essalud.apps.service.RequerimientoUsuarioService;

import java.net.URI;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(RequerimientoUsuarioController.REQUERIMIENTOS)
@RequiredArgsConstructor
public class RequerimientoUsuarioController {

    static final String REQUERIMIENTOS = "requerimientosUsuarios";
    private final RequerimientoUsuarioService requerimientoUsuarioService;
    private final RequerimientoService requerimientoService;

    @PostMapping("/registrar/requerimiento")
    public ResponseEntity<Object> registrar(@Valid @RequestBody Requerimiento requerimiento) {
        Requerimiento req = requerimientoService.registrar(requerimiento);

        URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(req.getIdRequerimiento()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("modificar/requerimiento/{idRequerimiento}")
    public void modificarRequerimiento(@PathVariable Integer idRequerimiento, @RequestBody Requerimiento request) {
        requerimientoService.modificarRequerimiento(idRequerimiento, request);
    }

    @PostMapping("tareas/registrar/transaccion")
    public ResponseEntity<Object> registrar(@Valid @RequestBody RequerimientoUsuario obj) {
        RequerimientoUsuario reqPer = requerimientoUsuarioService.registrar(obj);
        URI location =ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(reqPer.getIdRequerimientoUsuario()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/listar/principal")
    public ResponseEntity<List<RequerimientoUsuario>> listarRequerimientosIntegrantesPrincipal() {
        List<RequerimientoUsuario> lista = requerimientoUsuarioService.listarRequerimientosIntegrantesPrincipal();
        return new ResponseEntity<List<RequerimientoUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/pendiente")
    public ResponseEntity<List<RequerimientoUsuario>> listarRequerimientosPendientesPorUsuario() {
        List<RequerimientoUsuario> lista = requerimientoUsuarioService.listarRequerimientosPendientesPorUsuario();
        return new ResponseEntity<List<RequerimientoUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/finalizado")
    public ResponseEntity<List<RequerimientoUsuario>> listarRequerimientosFinalizadoPorUsuario() {
        List<RequerimientoUsuario> lista = requerimientoUsuarioService.listarRequerimientosFinalizadoPorUsuario();
        return new ResponseEntity<List<RequerimientoUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/rechazado")
    public ResponseEntity<List<RequerimientoUsuario>> listarRequerimientosRechazadoPorUsuario() {
        List<RequerimientoUsuario> lista = requerimientoUsuarioService.listarRequerimientosRechazadoPorUsuario();
        return new ResponseEntity<List<RequerimientoUsuario>>(lista, HttpStatus.OK);
    }


    @GetMapping("/aprobar")
    public int aprobarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
        return requerimientoUsuarioService.aprobarRequerimiento(estado, idRequerimientoUsuario);
    }

    @GetMapping("/rechazar")
    public int rechazarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
        return requerimientoUsuarioService.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
    }

    @GetMapping("/derivar")
    public int derivarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("codUnidadReceptor") String codUnidadReceptor, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
        return requerimientoUsuarioService.derivarRequerimiento(estado, motivo, codUnidadReceptor, idRequerimientoUsuario);
    }

    @GetMapping("/listar/unidadOrganizacion")
    public List<UnidadOrganizativa> listarRedes() {
        return requerimientoUsuarioService.listarRedes();
    }

    @GetMapping("/listar/requerimientos/{idUsuario}/personal")
    public ResponseEntity<List<RequerimientoUsuario>> listarRequerimientosPorPersonal(@PathVariable("idUsuario") Number idUsuario) {
        List<RequerimientoUsuario> lista = requerimientoUsuarioService.listarRequerimientosPorPersonal(idUsuario);
        return new ResponseEntity<List<RequerimientoUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/personal/red")
    public ResponseEntity<List<PersonalDTO>> listarPersonalPorRed() {
        List<PersonalDTO> lista = requerimientoUsuarioService.listarPersonalPorRed();
        return new ResponseEntity<List<PersonalDTO>>(lista, HttpStatus.OK);
    }

    @GetMapping("/finalizar/requerimiento")
    public int finalizarTareaAdministrador(@RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
        return requerimientoUsuarioService.finalizarTareaAdministrador(idRequerimientoUsuario);
    }

}
