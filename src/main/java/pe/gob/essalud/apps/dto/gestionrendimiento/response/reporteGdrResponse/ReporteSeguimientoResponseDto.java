package pe.gob.essalud.apps.dto.gestionrendimiento.response.reporteGdrResponse;

import lombok.Data;

@Data
public class ReporteSeguimientoResponseDto {
    private String numeroDocumento;
    private String nombreCompleto;
    private String unidad;
    private String puesto;
    private String rol;
    private String meta;
    private String mes;
    private String plazo;
}
