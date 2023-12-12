package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEvidenciaDto {
    private String descripcion;
    private LocalDateTime plazo;
}
