package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.personal.response.PersonalLogeoResponseDto;
import pe.gob.essalud.apps.dto.personal.response.PersonalTokenResponseDto;

public interface PersonalTokenService {
    PersonalTokenResponseDto generarToken();
    PersonalLogeoResponseDto validarToken(String token);
}
