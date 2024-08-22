package pe.gob.essalud.apps.dto.formencuesta.reponse;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuestaTrabajador;

import java.util.List;
@Data
public class FormRespuestaDto {
    private String nombreUsuarioContesta;
    private List<FormEncuestaTrabajador> listPreguntas;
}
