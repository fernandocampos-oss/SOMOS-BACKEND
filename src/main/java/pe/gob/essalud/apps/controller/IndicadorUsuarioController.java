package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.IndicadorUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoValorMeta;
import pe.gob.essalud.apps.service.IndicadorService;
import pe.gob.essalud.apps.service.IndicadorUsuarioService;

import java.net.URI;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(IndicadorUsuarioController.INDICADORES_USUARIOS)
@RequiredArgsConstructor
public class IndicadorUsuarioController {

    static final String INDICADORES_USUARIOS = "indicadores-usuarios";
    private final IndicadorUsuarioService indicadorUsuarioService;
    private final IndicadorService indicadorService;

    @PostMapping("/registrar/indicador")
    public ResponseEntity<Object> registrarIndicador(@Valid @RequestBody Indicador indicador) {
        Indicador result = indicadorService.registrarIndicador(indicador);

        URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getIdIndicador()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("modificar/indicador/{idIndicador}")
    public void modificarIndicador(@PathVariable Integer idIndicador, @RequestBody Indicador request) {
        indicadorService.modificarIndicador(idIndicador, request);
    }

    @PostMapping("tareas/registrar/transaccion")
    public ResponseEntity<Object> registrar(@Valid @RequestBody IndicadorUsuario obj) {
        IndicadorUsuario reqPer = indicadorUsuarioService.registrar(obj);
        URI location =ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(reqPer.getIdIndicadorUsuario()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/listar/principal")
    public ResponseEntity<List<IndicadorUsuario>> listarRequerimientosIntegrantesPrincipal() {
        List<IndicadorUsuario> lista = indicadorUsuarioService.listarRequerimientosIntegrantesPrincipal();
        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/pendiente")
    public ResponseEntity<List<IndicadorUsuario>> listarRequerimientosPendientesPorUsuario() {
        List<IndicadorUsuario> lista = indicadorUsuarioService.listarRequerimientosPendientesPorUsuario();
        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/finalizado")
    public ResponseEntity<List<IndicadorUsuario>> listarRequerimientosFinalizadoPorUsuario() {
        List<IndicadorUsuario> lista = indicadorUsuarioService.listarRequerimientosFinalizadoPorUsuario();
        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
    }

//    @GetMapping("/listar/rechazado")
//    public ResponseEntity<List<RequerimientoUsuario>> listarRequerimientosRechazadoPorUsuario() {
//        List<RequerimientoUsuario> lista = requerimientoUsuarioService.listarRequerimientosRechazadoPorUsuario();
//        return new ResponseEntity<List<RequerimientoUsuario>>(lista, HttpStatus.OK);
//    }

    @GetMapping("/aprobar")
    public int aprobarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
        return indicadorUsuarioService.aprobarRequerimiento(estado, idRequerimientoUsuario);
    }

//    @GetMapping("/rechazar")
//    public int rechazarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
//        return requerimientoUsuarioService.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
//    }

//    @GetMapping("/derivar")
//    public int derivarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("codUnidadReceptor") String codUnidadReceptor, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
//        return requerimientoUsuarioService.derivarRequerimiento(estado, motivo, codUnidadReceptor, idRequerimientoUsuario);
//    }

//    @GetMapping("/listar/unidadOrganizacion")
//    public List<UnidadOrganizativa> listarUnidad() {
//        return requerimientoUsuarioService.listarUnidad();
//    }

    @GetMapping("/listar/requerimientos/{idUsuario}/personal")
    public ResponseEntity<List<IndicadorUsuario>> listarRequerimientosPorPersonal(@PathVariable("idUsuario") Number idUsuario) {
        List<IndicadorUsuario> lista = indicadorUsuarioService.listarRequerimientosPorPersonal(idUsuario);
        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
    }
//
//    @GetMapping("/listar/personal/red")
//    public ResponseEntity<List<PersonalDTO>> listarPersonalPorRed() {
//        List<PersonalDTO> lista = requerimientoUsuarioService.listarPersonalPorRed();
//        return new ResponseEntity<List<PersonalDTO>>(lista, HttpStatus.OK);
//    }

    @GetMapping("/finalizar/requerimiento")
    public int finalizarTareaAdministrador(@RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
        return indicadorUsuarioService.finalizarTareaAdministrador(idRequerimientoUsuario);
    }

    @GetMapping("/reporte/excel/anio/{anio}")
    public ResponseEntity<List<IndicadorUsuario>> getAllRequerimientoUsuarioPorAnio(@PathVariable("anio") Number anio) {
        List<IndicadorUsuario> lista = indicadorUsuarioService.getAllRequerimientoUsuarioPorAnio(anio);
        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/tipoIngreso")
    public ResponseEntity<List<TipoIngreso>> getAllTipoIngreso() {
        List<TipoIngreso> lista = indicadorUsuarioService.getAllTipoIngreso();
        return new ResponseEntity<List<TipoIngreso>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/tipoValorMeta")
    public ResponseEntity<List<TipoValorMeta>> getAllTipoValorMeta() {
        List<TipoValorMeta> list = indicadorUsuarioService.getAllTipoValorMeta();
        return new ResponseEntity<List<TipoValorMeta>>(list, HttpStatus.OK);
    }



}
