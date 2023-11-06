package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvidenciaResponseDto {
    private String evidenciaDescripcion;
    private LocalDateTime evidenciaFechaRegistro;
    private String fileBase64;
    private String extension;
}
