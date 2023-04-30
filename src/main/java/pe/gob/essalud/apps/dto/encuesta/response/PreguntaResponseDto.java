package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

import java.util.List;

@Data
public class PreguntaResponseDto {

    private int idPregunta;
    private String descripcion;
    private List<AlternativaResponseDto> alternativas;

}
