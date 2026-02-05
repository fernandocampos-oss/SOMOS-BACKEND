package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Prioridad;

import java.util.List;

@Data
public class PrioridadExistRequestDto {
    private Prioridad prioridad;
    private Votante votante;
    private Indicador indicador;
    private List<Evidencia> listEvidencia;
    private String sentidoIndicador;
    private String fechaPlazoFinal;
}
