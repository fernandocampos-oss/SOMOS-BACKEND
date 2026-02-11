package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelTrabajadorDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.PendienteDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.IndicadorService;
import pe.gob.essalud.apps.service.gdr.SentidoIndicadorService;
import pe.gob.essalud.apps.service.gdr.EvidenciaTipoService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(IndicadorController.INDICADOR)
@RequiredArgsConstructor
public class IndicadorController {

    static final String INDICADOR = "indicadores";
    private final IndicadorService indicadorService;
    private final SentidoIndicadorService sentidoIndicadorService;
    private final EvidenciaTipoService evidenciaTipoService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarIndicador(@Valid @RequestBody IndicadorRequestDto requestDto) {
        try {
            // Guardar indicador y obtener el ID
            Integer idIndicador = indicadorService.registrarIndicador(requestDto);
            log.info("Indicador registrado con ID: {}", idIndicador);
            
            // Guardar sentido del indicador en transacción separada
            if (requestDto.getSentidoIndicador() != null && !requestDto.getSentidoIndicador().isEmpty()) {
                try {
                    log.info("Guardando sentido del indicador: {} para ID: {}", requestDto.getSentidoIndicador(), idIndicador);
                    sentidoIndicadorService.guardarOActualizar(idIndicador.longValue(), requestDto.getSentidoIndicador());
                    log.info("Sentido guardado exitosamente");
                } catch (Exception e) {
                    log.error("Error al guardar sentido: {}", e.getMessage(), e);
                }
            }
            
            // Guardar fecha de plazo final en transacción separada
            if (requestDto.getFechaPlazoFinal() != null && !requestDto.getFechaPlazoFinal().isEmpty()) {
                try {
                    LocalDate fechaPlazo = LocalDate.parse(requestDto.getFechaPlazoFinal().substring(0, 10));
                    log.info("Guardando fecha plazo final: {} para ID: {}", fechaPlazo, idIndicador);
                    evidenciaTipoService.guardarFechaPlazoFinalPorIndicador(idIndicador.longValue(), fechaPlazo);
                    log.info("Fecha plazo guardada exitosamente");
                } catch (Exception e) {
                    log.error("Error al guardar fecha plazo: {}", e.getMessage(), e);
                }
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al registrar indicador: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage() != null ? e.getMessage() : "Error desconocido"
            ));
        }
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

    @GetMapping("/excel/trabajador/admin/{idVotante}")
    public ResponseEntity<ExcelTrabajadorDto> generarExcelTrabajadorByVotanteAdmin(@PathVariable int idVotante) {
        ExcelTrabajadorDto model = indicadorService.generarExcelTrabajadorByVotanteAdmin(idVotante);
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

    @DeleteMapping("/eliminar/{id}")
    public void eliminarIndicador(@PathVariable int id) {
        indicadorService.eliminarIndicador(id);
    }

}
