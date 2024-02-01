package pe.gob.essalud.apps.dto.usuario.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UsuarioCambiarCorreoRequestDto {

    @NotEmpty(message = "el token es obligatorio")
    private String token;
    @NotEmpty(message = "El nuevo correo es obligatorio")
    private String nuevoCorreo;

}
