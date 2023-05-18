package pe.gob.essalud.apps.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    UserResponseDto user;
    String token;

}