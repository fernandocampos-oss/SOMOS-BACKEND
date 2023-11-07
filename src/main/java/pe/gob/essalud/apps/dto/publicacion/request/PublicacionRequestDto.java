package pe.gob.essalud.apps.dto.publicacion.request;

import lombok.Data;
import pe.gob.essalud.apps.dto.inscripcion.request.CreateInscripcionRequestDto;

import java.util.List;

@Data
public class PublicacionRequestDto {

    private String titulo;
    private String descripcion;
    private String urlRedireccion;
    private String imagenBase64;
    private int alcance;
    private boolean anuncio;
    private CreateInscripcionRequestDto inscripcionRequest;
    private List<String> redes;

}
