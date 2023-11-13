package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;

@Data
public class PrioridadDto {
    private Actividad actividad;
    private int[] listIdIndicador;
}
