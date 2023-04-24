package pe.gob.essalud.apps.dto.auth.request;

import lombok.Data;

@Data
public class TokenActivacionRequestDto {

    private long idUsuario;
    private String token;

}
