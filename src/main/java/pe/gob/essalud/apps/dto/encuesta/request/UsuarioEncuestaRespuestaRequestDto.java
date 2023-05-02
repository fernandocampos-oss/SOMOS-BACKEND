package pe.gob.essalud.apps.dto.encuesta.request;

import lombok.Data;

@Data
public class UsuarioEncuestaRespuestaRequestDto {

    private int idPregunta;
    private int idAlternativa;

}
