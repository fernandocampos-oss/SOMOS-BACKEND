package pe.gob.essalud.apps.dto.encuesta.request;

import lombok.Data;

import java.util.List;

@Data
public class UsuarioEncuestaRequestDto {

    private int idSede;
    private int idGrupoPersonal;
    private int idAreaPersonal;
    private int idTiempoServicio;
    private List<UsuarioEncuestaRespuestaRequestDto> respuestas;

}
