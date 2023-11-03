package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

import javax.persistence.Column;

@Data
public class InscripcionResponseDto {

    private Integer idInscripcion;
    private String descripcion;
    private boolean enviado;
}
