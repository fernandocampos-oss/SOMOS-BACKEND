package pe.gob.essalud.apps.dto.gestionrendimiento.request.reporteGdrRequest;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReporteSeguimientoRequestDto {
    private int anio;
    private ArrayList<String> listCodRed;
    private String codUnidad;
    private Boolean allRed;
}
