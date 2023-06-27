package pe.gob.essalud.apps.dto.emailservice;

import lombok.Data;

@Data
public class RecuperarClaveWebRequestDto {
    private String email;
    private String url;
}
