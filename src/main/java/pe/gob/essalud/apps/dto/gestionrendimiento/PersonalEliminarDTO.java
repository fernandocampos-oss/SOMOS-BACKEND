package pe.gob.essalud.apps.dto.gestionrendimiento;

import lombok.Data;

@Data
public class PersonalEliminarDTO {
    private Number idEstadoPersonal;
    private String motivo;
    private Number idPersonal;
}
