package pe.gob.essalud.apps.dto.emailservice;

import lombok.Data;

@Data
public class ActivarCuentaRequestDto {
    private String email;
    private String token;
}
