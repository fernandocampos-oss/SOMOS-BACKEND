package pe.gob.essalud.apps.dto.publicacion.request;

import lombok.Data;

@Data
public class PublicacionRequestDto {

    private String titulo;
    private String descripcion;
    private String urlRedireccion;
    private String imagenBase64;

}
