package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

import java.util.List;

@Data
public class InscripcionVotacionResponseDto {

    private Integer idInscripcion;
    private String descripcion;
    List<IncripcionPersonaResponseDto> candidatos;

}
