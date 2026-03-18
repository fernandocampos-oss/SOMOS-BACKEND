package pe.gob.essalud.apps.controller.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.common.util.XlsReport;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.DashboardAvanceRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.DashboardAvanceDto;
import pe.gob.essalud.apps.service.gdr.DashboardAvanceService;

import java.util.List;

@RestController
@RequestMapping("/gdr/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardAvanceController {

    private final DashboardAvanceService dashboardAvanceService;

    /**
     * Obtener dashboard de avance GDR
     * @param request Filtros: anio, listCodRed, codUnidad, tipoAgrupacion (PERSONA, UNIDAD, ORGANO)
     * @return Lista de datos de avance según agrupación
     */
    @PostMapping("/avance")
    public ResponseEntity<List<DashboardAvanceDto>> obtenerDashboard(@RequestBody DashboardAvanceRequestDto request) {
        try {
            log.info("Solicitando dashboard de avance: {}", request);
            List<DashboardAvanceDto> resultado = dashboardAvanceService.obtenerDashboard(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener dashboard de avance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exportar dashboard a Excel
     * @param request Filtros para el dashboard
     * @return Archivo Excel con los datos del dashboard
     */
    @PostMapping("/avance/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody DashboardAvanceRequestDto request) {
        try {
            log.info("Exportando dashboard a Excel: {}", request);
            List<DashboardAvanceDto> datos = dashboardAvanceService.obtenerDashboard(request);

            String sheetName = "Dashboard GDR";
            String titulo = "Dashboard de Avance GDR - Año " + request.getAnio();
            String headers;
            String fields;

            // Ajustar columnas según tipo de agrupación
            switch (request.getTipoAgrupacion().toUpperCase()) {
                case "PERSONA":
                    headers = "Nro. Documento|Nombre Completo|Indicadores|Ind. Completados|Evid. Iniciales|Evid. Finales|Total Evidencias|Evid. Subidas|F. Planeación|F. Seguimiento|F. Evaluación|% Avance";
                    fields = "Codigo|Nombre|TotalIndicadores|IndicadoresCompletados|EvidenciasIniciales|EvidenciasFinales|TotalEvidencias|EvidenciasSubidas|FasePlaneacion|FaseSeguimiento|FaseEvaluacion|PorcentajeAvance";
                    break;
                case "UNIDAD":
                    headers = "Código|Unidad Organizativa|Trabajadores|Indicadores|Ind. Completados|Evid. Iniciales|Evid. Finales|Total Evidencias|Evid. Subidas|F. Planeación|F. Seguimiento|F. Evaluación|% Avance";
                    fields = "Codigo|Nombre|TotalTrabajadores|TotalIndicadores|IndicadoresCompletados|EvidenciasIniciales|EvidenciasFinales|TotalEvidencias|EvidenciasSubidas|FasePlaneacion|FaseSeguimiento|FaseEvaluacion|PorcentajeAvance";
                    break;
                case "ORGANO":
                default:
                    headers = "Código|Órgano|Trabajadores|Indicadores|Ind. Completados|Evid. Iniciales|Evid. Finales|Total Evidencias|Evid. Subidas|F. Planeación|F. Seguimiento|F. Evaluación|% Avance";
                    fields = "Codigo|Nombre|TotalTrabajadores|TotalIndicadores|IndicadoresCompletados|EvidenciasIniciales|EvidenciasFinales|TotalEvidencias|EvidenciasSubidas|FasePlaneacion|FaseSeguimiento|FaseEvaluacion|PorcentajeAvance";
                    break;
            }

            byte[] excelBytes = XlsReport.getReporte(sheetName, headers, titulo, fields, datos, DashboardAvanceDto.class);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            responseHeaders.setContentDispositionFormData("attachment", "dashboard_avance_gdr_" + request.getAnio() + ".xlsx");
            responseHeaders.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar dashboard a Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
