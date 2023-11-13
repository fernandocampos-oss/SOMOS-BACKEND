package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

import java.util.List;

@Data
public class TareaRequestDto {
    private Actividad actividad;
    //    private IndicadorUsuario indicadorUsuario;
    private Indicador indicador;
    private List<Tarea> listTarea;
}
