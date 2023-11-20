package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;

import java.util.List;

@Data
public class IndicadorExistRequestDto {
    private Indicador indicador;
    private List<Evidencia> listEvidencia;
}
