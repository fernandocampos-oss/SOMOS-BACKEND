package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

@Data
public class InscripcionResponseDto {

    private Integer idInscripcion;
    private String descripcion;
    private boolean enviado;
}
