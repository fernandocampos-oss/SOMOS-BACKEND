package pe.gob.essalud.apps.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequestDto {

    private final String usuario;
    private final String clave;

}
