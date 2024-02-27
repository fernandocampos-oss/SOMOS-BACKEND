package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

@Data
public class MainPrioridadDto {
    private int idPrioridad;
    private String prioridadNombre;
    private int idActividad;
    private LocalDateTime fechaAsignacionPrioridad;
    private List<MainIndicadorDto> listIndicador;
}
