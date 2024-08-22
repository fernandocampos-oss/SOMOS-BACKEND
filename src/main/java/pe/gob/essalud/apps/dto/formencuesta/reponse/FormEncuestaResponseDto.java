package pe.gob.essalud.apps.dto.formencuesta.reponse;

import lombok.Data;
import java.util.List;
@Data
public class FormEncuestaResponseDto {
    private Integer idFormEncuesta;
    private String publicacionNombre;
    private int idUsuarioCreacion;

    private List<FormRespuestaDto> listUsuarios;
}
