package pe.gob.essalud.apps.dto.gestionrendimiento;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

import java.util.List;

@Data
public class EvidenciaRequestDTO {

    private int idEvidencia;
    private String descripcion;
    private int porcentajeAvance;
    private Tarea tarea;
    private String imagenBase64;

    private String nombreImagen;
    private int sizeImagen;
    private String tipoImagen;
    private String extension;
}
