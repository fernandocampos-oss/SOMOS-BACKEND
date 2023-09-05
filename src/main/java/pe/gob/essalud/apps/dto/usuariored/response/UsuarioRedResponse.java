package pe.gob.essalud.apps.dto.usuariored.response;

import lombok.Data;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;

import java.time.LocalDateTime;

@Data
public class UsuarioRedResponse {

    private UsuarioNombresResponse usuario;
    private LocalDateTime fechaAsignacion;
    private boolean habilitado;
    private RedResponse red;

}
