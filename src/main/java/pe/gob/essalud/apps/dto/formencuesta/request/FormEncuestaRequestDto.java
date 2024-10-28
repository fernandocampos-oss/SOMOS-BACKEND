package pe.gob.essalud.apps.dto.formencuesta.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormPregunta;

import java.util.List;
@Data
public class FormEncuestaRequestDto {
    private int idUsuarioCreacion;
    private int idUsuarioContesta;
    private int fechaRespuesta;
    private boolean finalizado;
    private List<FormPregunta> listPregunta;
}
