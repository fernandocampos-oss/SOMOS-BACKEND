package pe.gob.essalud.apps.dto.usuario.request;

import lombok.Data;

@Data
public class UsuarioActualizarDatosRequestDto {

    private String correo;
    private String numeroCelular;
    private String imagenPerfilBase64;
    private String imagenFirmaBase64;

}
