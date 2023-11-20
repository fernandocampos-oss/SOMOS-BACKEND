package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class PendienteDto {
    private int idPrioridad;
    private String prioridadNombre;
    private List<PendienteIndicadorDto> listIndicador;
}
