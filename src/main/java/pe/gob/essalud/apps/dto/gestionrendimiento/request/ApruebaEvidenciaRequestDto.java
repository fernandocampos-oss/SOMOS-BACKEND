package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;

@Data
public class ApruebaEvidenciaRequestDto {
    Integer idEvidencia;
    String comentario;
    Integer calificacion;
}
