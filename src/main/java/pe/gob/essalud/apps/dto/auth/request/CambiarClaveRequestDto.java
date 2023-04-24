package pe.gob.essalud.apps.dto.auth.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CambiarClaveRequestDto {

    @NotEmpty(message = "el token es obligatorio")
    private String token;
    @NotEmpty(message = "el usuario es obligatorio")
    private String username;
    @NotEmpty(message = "La nueva clave es obligatorio")
    private String nuevaClave;

}
