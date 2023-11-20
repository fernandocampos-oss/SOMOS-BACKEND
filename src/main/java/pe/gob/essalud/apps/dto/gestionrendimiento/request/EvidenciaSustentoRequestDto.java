package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;

@Data
public class EvidenciaSustentoRequestDto {
    private int idEvidencia;
    private String sustentoDescripcion;
    private String extension;
    private String fileBase64;
}
