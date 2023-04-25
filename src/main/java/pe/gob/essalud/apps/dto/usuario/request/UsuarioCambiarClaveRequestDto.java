package pe.gob.essalud.apps.dto.usuario.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UsuarioCambiarClaveRequestDto {

    @NotEmpty(message = "La actual clave es obligatorio")
    private String actualClave;
    @NotEmpty(message = "La nueva clave es obligatorio")
    private String nuevaClave;

}
