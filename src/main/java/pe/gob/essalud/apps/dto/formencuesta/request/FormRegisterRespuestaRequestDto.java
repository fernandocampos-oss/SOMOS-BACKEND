package pe.gob.essalud.apps.dto.formencuesta.request;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormPregunta;

import java.util.List;
@Data
public class FormRegisterRespuestaRequestDto {
    private Integer idFormEncuesta;
    private List<FormPregunta> listPregunta;

}
