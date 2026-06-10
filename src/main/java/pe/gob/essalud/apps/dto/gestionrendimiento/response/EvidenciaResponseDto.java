package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvidenciaResponseDto {
    private String evidenciaDescripcion;
    private LocalDateTime evidenciaFechaRegistro;
    private String fileBase64;
    private String extension;
    private String comentario;
    private Integer calificacion;
    // true cuando el archivo fue subido con el sistema anterior (ruta local) y ya no está disponible
    private boolean esArchivoAntiguo;
}
