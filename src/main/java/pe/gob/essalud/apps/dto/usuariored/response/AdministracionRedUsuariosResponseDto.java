package pe.gob.essalud.apps.dto.usuariored.response;

import lombok.Data;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioInformacionResponseDto;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;

import java.util.List;

@Data
public class AdministracionRedUsuariosResponseDto {

    private List<UsuarioInformacionResponseDto> usuarios;
    private List<RedPersonal> redes;
}
