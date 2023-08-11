package pe.gob.essalud.apps.dto.usuariored.request;

import lombok.Data;

import java.util.List;

@Data
public class UsuarioRedRequest {

    private long idUsuario;
    private List<String> redes;

}
