package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PendienteEvidenciaDto {
    private int idEvidencia;
    private String descripcion;
    private LocalDateTime plazo;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private String sustentoDescripcion;
    private LocalDateTime sustentoFechaRegistro;
    private String sustentoExtensionFile;
    private int calificacion;
}
