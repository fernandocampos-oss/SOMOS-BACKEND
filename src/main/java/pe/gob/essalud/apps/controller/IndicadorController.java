package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelTrabajadorDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.PendienteDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.IndicadorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(IndicadorController.INDICADOR)
@RequiredArgsConstructor
public class IndicadorController {

    static final String INDICADOR = "indicadores";
    private final IndicadorService indicadorService;

    @PostMapping("/registrar")
    public void registrarIndicador(@Valid @RequestBody IndicadorRequestDto requestDto) {
        indicadorService.registrarIndicador(requestDto);
    }

    @GetMapping("/listar/pendientes")
    public ResponseEntity<List<PendienteDto>> listPendientesTrabajadorByUser() {
        List<PendienteDto> lista = indicadorService.listPendientesTrabajadorByUser();
        return new ResponseEntity<List<PendienteDto>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/pendientes/admin/{idVotante}")
    public ResponseEntity<List<PendienteDto>> listPendientesTrabajadorByVotanteAdmin(@PathVariable int idVotante) {
        List<PendienteDto> lista = indicadorService.listPendientesTrabajadorByVotanteAdmin(idVotante);
        return new ResponseEntity<List<PendienteDto>>(lista, HttpStatus.OK);
    }

    @GetMapping("/listar/tipoValorMeta")
    public ResponseEntity<List<TipoValorMeta>> getAllTipoValorMeta() {
        List<TipoValorMeta> list = indicadorService.getAllTipoValorMeta();
        return new ResponseEntity<List<TipoValorMeta>>(list, HttpStatus.OK);
    }

    @GetMapping("/excel/trabajador")
    public ResponseEntity<ExcelTrabajadorDto> generarExcelTrabajador() {
        ExcelTrabajadorDto model = indicadorService.generarExcelTrabajador();
        return new ResponseEntity<ExcelTrabajadorDto>(model, HttpStatus.OK);
    }

    @GetMapping("/peso-total/{idVotante}")
    public Optional<Integer> sumaTotalPesoAllIndicadorByTrabajador(@PathVariable int idVotante) {
        return indicadorService.sumaTotalPesoAllIndicadorByTrabajador(idVotante);
    }

    @PutMapping("modificar/{id}")
    public void modificarIndicador(@PathVariable Integer id, @RequestBody Indicador request) {
        indicadorService.modificarIndicador(id, request);
    }

}
