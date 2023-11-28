package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExcelEvidenciaDto {
    private int idEvidencia;
    private String descripcion;
    private LocalDateTime plazo;

    private LocalDateTime fechaCreacion;
    private String sustentoDescripcion;
    private LocalDateTime sustentoFechaRegistro;
    private String sustentoExtensionFile;
}
