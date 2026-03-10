package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExcelEvidenciaDto {
    private int idEvidencia;
    private String descripcion;
    private LocalDateTime plazo;
    private String comentario;
    private String estadoDropdown;       // "Logrado", "En Proceso", etc. (de comentario_estado)
    private String comentarioAdicional;  // Texto adicional (de comentario_estado)
    private LocalDateTime fechaCreacion;
    private String sustentoDescripcion;
    private LocalDateTime sustentoFechaRegistro;
    private String sustentoExtensionFile;
    private boolean esEvidenciaFinal; // Para identificar SUSTENTO FINAL
}
