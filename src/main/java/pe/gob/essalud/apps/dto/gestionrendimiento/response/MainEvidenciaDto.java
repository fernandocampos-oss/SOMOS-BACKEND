package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MainEvidenciaDto {
    private int idTarea;
    private String descripcion;
    private LocalDateTime plazo;

    private LocalDateTime fechaCreacion;
    private String sustentoDescripcion;
    private LocalDateTime sustentoFechaRegistro;
    private String sustentoExtensionFile;
}
