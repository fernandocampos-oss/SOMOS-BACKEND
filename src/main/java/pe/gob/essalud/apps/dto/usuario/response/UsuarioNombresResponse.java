package pe.gob.essalud.apps.dto.usuario.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioNombresResponse {

    private long idUsuario;
    private String nombresCompletos;

}
