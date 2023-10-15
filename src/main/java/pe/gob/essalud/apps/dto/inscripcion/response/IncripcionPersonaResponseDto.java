package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

@Data
public class IncripcionPersonaResponseDto {

    private long idInsPersona;
    private String descripcion;
    private String imagenBase64;

}
