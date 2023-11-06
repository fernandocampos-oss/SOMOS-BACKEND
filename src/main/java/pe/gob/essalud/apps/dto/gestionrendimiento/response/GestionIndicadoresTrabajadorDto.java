package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.IndicadorUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

import java.util.List;

@Data
public class GestionIndicadoresTrabajadorDto {
    private String trabajadorNombre;
    private String trabajadorApellido;
    private List<IndicadorUsuario> listIndicador;
}
