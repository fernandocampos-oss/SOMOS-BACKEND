package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;

import java.time.LocalDateTime;

@Data
public class UpdateVotanteDto {
    private int idSegmento;
    private String codCondicion;
}

