package pe.gob.essalud.apps.dto.inscripcion.request;

import lombok.Data;
import pe.gob.essalud.apps.dto.formencuesta.request.FormEncuestaRequestDto;

import java.util.List;

@Data
public class CreateInscripcionRequestDto {

    private boolean imagenActiva;
    private String imagenDescripcion;
    private boolean textoActivo;
    private String textoDescripcion;
    private boolean grupoActivo;
    private Integer grupoLongitud;
    private boolean esVotacion;
    private List<Integer> usuarios;

//    private boolean encuestaActivo;
//    private FormEncuestaRequestDto formEncuestaRequestDto;
}
