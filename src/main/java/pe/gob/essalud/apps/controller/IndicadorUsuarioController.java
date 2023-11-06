package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.GestionIndicadoresTrabajadorDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
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

    @GetMapping("/listar/indicadores-tareas/principal")
    public ResponseEntity<List<GestionIndicadoresTrabajadorDto>> listarTrabajadoresIndicadoresJefePrincipal() {
        List<GestionIndicadoresTrabajadorDto> lista = indicadorUsuarioService.listarTrabajadoresIndicadoresJefePrincipal();
        return new ResponseEntity<List<GestionIndicadoresTrabajadorDto>>(lista, HttpStatus.OK);
    }

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

    @GetMapping("/listar/pendiente")
    public ResponseEntity<List<IndicadorUsuario>> listarIndicadoresPendientesPorUsuario() {
        List<IndicadorUsuario> lista = indicadorUsuarioService.listarIndicadoresPendientesPorUsuario();
        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/finalizado")
    public ResponseEntity<List<IndicadorUsuario>> listarIndicadoresFinalizadoPorUsuario() {
        List<IndicadorUsuario> lista = indicadorUsuarioService.listarIndicadoresFinalizadoPorUsuario();
        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
    }

//    @GetMapping("/aprobar")
//    public int aprobarIndicador(@RequestParam("estado") Number estado, @RequestParam("idIndicadorUsuario") Number idIndicadorUsuario) {
//        return indicadorUsuarioService.aprobarIndicador(estado, idIndicadorUsuario);
//    }

//    @GetMapping("/rechazar")
//    public int rechazarRequerimiento(@RequestParam("estado") Number estado, @RequestParam("motivo") String motivo, @RequestParam("idRequerimientoUsuario") Number idRequerimientoUsuario) {
//        return requerimientoUsuarioService.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
//    }

//    @GetMapping("/listar/indicadores/{idUsuario}/trabajador")
//    public ResponseEntity<List<IndicadorUsuario>> getlistIndicadoresByIdUsuario(@PathVariable("idUsuario") Number idUsuario) {
//        List<IndicadorUsuario> lista = indicadorUsuarioService.getlistIndicadoresByIdUsuario(idUsuario);
//        return new ResponseEntity<List<IndicadorUsuario>>(lista, HttpStatus.OK);
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

    @GetMapping("/listar/actividades")
    public ResponseEntity<List<Actividad>> getAllActividades() {
        List<Actividad> lista = indicadorUsuarioService.getAllActividades();
        return new ResponseEntity<List<Actividad>>(lista, HttpStatus.OK);
    }

}
