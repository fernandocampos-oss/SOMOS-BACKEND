package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class MainPrioridadDto {
    private int idPrioridad;
    private String prioridadNombre;
    private int peso;
    private List<MainIndicadorDto> listIndicador;
}
