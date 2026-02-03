package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;

import java.util.List;

@Data
public class IndicadorRequestDto {
    private Actividad actividad;
    private Votante votante;
    private Indicador indicador;
    private List<Evidencia> listEvidencia;
    private String sentidoIndicador;  // 'ascendente' o 'descendente'
    private String fechaPlazoFinal;   // Fecha de plazo final para la evidencia final
}
