package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.IndicadorService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(IndicadorController.INDICADOR)
@RequiredArgsConstructor
public class IndicadorController {

    static final String INDICADOR = "indicadores";
    private final IndicadorService indicadorService;

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrarIndicador(@Valid @RequestBody Indicador indicador) {
        Indicador result = indicadorService.registrarIndicador(indicador);
        URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getIdIndicador()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/listar/pendientes")
    public ResponseEntity<List<Indicador>> getListIndicadoresPendientesByUser() {
        List<Indicador> lista = indicadorService.getListIndicadoresPendientesByUser();
        return new ResponseEntity<List<Indicador>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/tipoIngreso")
    public ResponseEntity<List<TipoIngreso>> getAllTipoIngreso() {
        List<TipoIngreso> lista = indicadorService.getAllTipoIngreso();
        return new ResponseEntity<List<TipoIngreso>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/tipoValorMeta")
    public ResponseEntity<List<TipoValorMeta>> getAllTipoValorMeta() {
        List<TipoValorMeta> list = indicadorService.getAllTipoValorMeta();
        return new ResponseEntity<List<TipoValorMeta>>(list, HttpStatus.OK);
    }

    @PutMapping("modificar/indicador/{idIndicador}")
    public void modificarIndicador(@PathVariable Integer idIndicador, @RequestBody Indicador request) {
        indicadorService.modificarIndicador(idIndicador, request);
    }


    @GetMapping("/listar/finalizado")
    public ResponseEntity<List<Indicador>> getListIndicadoresFinalizadoByUser() {
        List<Indicador> lista = indicadorService.getListIndicadoresFinalizadoByUser();
        return new ResponseEntity<List<Indicador>>(lista, HttpStatus.OK);
    }

}
