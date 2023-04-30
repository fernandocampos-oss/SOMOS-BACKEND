package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

import java.util.List;

@Data
public class EncuestaResponseDto {

    private int idEncuesta;
    private String descripcion;
    private List<PreguntaResponseDto> preguntas;
    private DatosDemograficosResponseDto datosDemograficos;

}