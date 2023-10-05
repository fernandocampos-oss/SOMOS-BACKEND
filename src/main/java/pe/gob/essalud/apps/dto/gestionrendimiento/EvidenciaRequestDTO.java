package pe.gob.essalud.apps.dto.gestionrendimiento;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

@Data
public class EvidenciaRequestDTO {
    private int idEvidencia;
    private String descripcion;
    private int porcentajeAvance;
    private Tarea tarea;
    private String imagenBase64;
    private String extension;
    private int idRequerimiento;
}
