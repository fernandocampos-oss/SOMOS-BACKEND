package pe.gob.essalud.apps.dto.personal.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonalTokenResponseDto {
    private String token;
    private String urlPlataforma;
    private long expiresIn; // segundos hasta expiración
}
