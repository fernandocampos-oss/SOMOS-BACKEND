package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class ExcelPrioridadDto {
    private int idPrioridad;
    private String prioridadNombre;
    private List<ExcelIndicadorDto> listIndicador;
}
