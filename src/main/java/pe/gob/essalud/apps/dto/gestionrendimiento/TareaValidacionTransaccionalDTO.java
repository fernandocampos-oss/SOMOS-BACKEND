package pe.gob.essalud.apps.dto.gestionrendimiento;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoPersonal;

import java.util.List;

@Data
public class TareaValidacionTransaccionalDTO {

    private RequerimientoPersonal requerimientoPersonal;
    private List<TareaValidacionDTO> listTareaDTO;

}
