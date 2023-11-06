package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;

@Data
public class EvidenciaRequestDto {
    private int idTarea;
    private String evidenciaDescripcion;
    private String fileBase64;
    private String extension;
}
