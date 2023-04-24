package pe.gob.essalud.apps.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class GenerarTokenRecuperarClaveResponseDto {

    @NotEmpty(message = "El correo es obligatorio")
    private String correo;

}
