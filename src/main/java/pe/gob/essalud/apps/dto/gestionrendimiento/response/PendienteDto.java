package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PendienteDto {
    private int idPrioridad;
    private String prioridadNombre;
    private int idActividad;
    private LocalDateTime fechaAsignacionPrioridad;
    private int peso;
    private List<PendienteIndicadorDto> listIndicador;
}
