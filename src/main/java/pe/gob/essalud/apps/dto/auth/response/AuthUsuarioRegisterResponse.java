package pe.gob.essalud.apps.dto.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUsuarioRegisterResponse {

    private String token;
    private long idUsuario;

}
