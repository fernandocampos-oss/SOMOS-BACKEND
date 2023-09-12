package pe.gob.essalud.apps.dto.proyecto.response;

import lombok.Data;
import pe.gob.essalud.apps.dto.proyecto.request.ProyectoRequest;

import java.util.List;

@Data
public class BandejaProyectosResponse {

    private int total;
    private int enviados;
    private int pendientesEnvio;
    private List<ProyectoRequest> proyectos;

}
