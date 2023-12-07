package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEvidenciaDto {
//    private int idEvidencia;
    private String descripcion;
    private LocalDateTime plazo;
}
