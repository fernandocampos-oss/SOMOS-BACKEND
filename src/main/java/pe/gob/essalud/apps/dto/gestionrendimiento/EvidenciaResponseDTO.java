package pe.gob.essalud.apps.dto.gestionrendimiento;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvidenciaResponseDTO {

    private long idEvidencia;
    private String descripcion;
    private int porcentajeAvance;
    private LocalDateTime fechaCreacion;
//    private boolean estado;
    private String imagenBase64;
    private String extension;
    private String nombreImagen;
}
