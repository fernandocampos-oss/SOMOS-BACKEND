package pe.gob.essalud.apps.dto.gestionrendimiento;

import java.util.List;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Personal;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

@Data
public class TareaValidacionDTO {
    private Personal personal;
    private List<Tarea> listTarea;
}
