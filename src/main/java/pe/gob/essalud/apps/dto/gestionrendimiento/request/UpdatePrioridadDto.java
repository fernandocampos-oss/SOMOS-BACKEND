package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;

import java.time.LocalDateTime;

@Data
public class UpdatePrioridadDto {
    private Actividad actividad;
    private LocalDateTime fechaAsignacion;
    private String prioridadNombre;
}
