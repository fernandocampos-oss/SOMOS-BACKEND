package pe.gob.essalud.apps.dto.gestionrendimiento.request.reporteGdrRequest;

import lombok.Data;

@Data
public class ReporteMatrizRequestDto {
    private int anio;
    private String codRed;
    private String codUnidad;
}
