package pe.gob.essalud.apps.dto.publicacion.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublicacionResponseDto {

    private int idPublicacion;
    private String titulo;
    private String descripcion;
    private String imagenBase64;
    private String urlRedireccion;
    private LocalDateTime fechaCreacion;
    private int tipoAlcance;
    private boolean anuncio;
    private boolean votacion;
    private boolean votoActivo;
    private int idInscripcion;
    private List<String> redes;

//    private boolean encuestaActivo;
//    private int idEncuesta;
}
