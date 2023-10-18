package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

@Data
public class ReporteInscVotacionResponseDto {

    private Integer idInsPersona;
    private Integer idLider;
    private Integer votos;
}
