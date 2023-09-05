package pe.gob.essalud.apps.dto.gestionrendimiento;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Poi;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

import java.util.List;

@Data
public class TareaDTO {
    private Poi poi;
    private RequerimientoUsuario requerimientoUsuario;
    private List<Tarea> listTarea;

}
