package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

import java.util.List;

@Data
public class ReportePreguntaResponseDto {

    private Integer idPregunta;
    private String descripcion;
    private List<ReporteAlternativaResponseDto> alternativas;
}
