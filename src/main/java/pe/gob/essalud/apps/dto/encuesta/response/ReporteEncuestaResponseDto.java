package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

import java.util.List;

@Data
public class ReporteEncuestaResponseDto {

    private Integer idEncuesta;
    private String nombreEncuesta;
    private Integer cantidadRespuestas;
    private List<ReportePreguntaResponseDto> preguntas;
    private List<ReporteUsuarioEncuestaResponseDto> usuarios;
}
