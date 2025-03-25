package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelTrabajadorDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.PendienteDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.IndicadorService;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @GetMapping("/excel/trabajador/admin/{idVotante}")
    public ResponseEntity<ExcelTrabajadorDto> generarExcelTrabajadorByVotanteAdmin(@PathVariable int idVotante) {
        ExcelTrabajadorDto model = indicadorService.generarExcelTrabajadorByVotanteAdmin(idVotante);
        return new ResponseEntity<ExcelTrabajadorDto>(model, HttpStatus.OK);
    }

    @GetMapping("/excel/trabajador/download")
    public ResponseEntity<Resource> downloadExcelTrabajador() {
        try {
            ExcelTrabajadorDto model = indicadorService.generarExcelTrabajador();
            ByteArrayResource resource = indicadorService.generateFormatoExcel(model);
            String filename = "GDR_" + model.getEvaluadoNombreCompleto() + ".xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/excel/trabajador/admin/download/{idVotante}")
    public ResponseEntity<Resource> downloadExcelTrabajadorByVotanteAdmin(@PathVariable int idVotante) {
        try {
            ExcelTrabajadorDto model = indicadorService.generarExcelTrabajadorByVotanteAdmin(idVotante);
            ByteArrayResource resource = indicadorService.generateFormatoExcel(model);
            String filename = "GDR_" + model.getEvaluadoNombreCompleto() + ".xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/excel/directivo/download")
    public ResponseEntity<Resource> downloadExcelDirectivo() {
        try {
            ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(zipOutputStream);

            List<ExcelTrabajadorDto> modelos = indicadorService.generarExcelDirectivo();
            for (ExcelTrabajadorDto model : modelos) {
                ByteArrayResource excelResource = indicadorService.generateFormatoExcel(model);
                String filename = "GDR_" + model.getEvaluadoNombreCompleto() + ".xlsx";
                ZipEntry zipEntry = new ZipEntry(filename);
                zipOut.putNextEntry(zipEntry);
                zipOut.write(excelResource.getByteArray());
                zipOut.closeEntry();
            }

            zipOut.close();
            ByteArrayResource zipResource = new ByteArrayResource(zipOutputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FORMATOS_GDR.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipResource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
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
