package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import java.util.List;

@Data
public class DashboardAvanceRequestDto {
    private String anio;
    private List<String> listCodRed;
    private String codUnidad;
    private String tipoAgrupacion; // PERSONA, UNIDAD, ORGANO
    private Boolean allRed;
}
