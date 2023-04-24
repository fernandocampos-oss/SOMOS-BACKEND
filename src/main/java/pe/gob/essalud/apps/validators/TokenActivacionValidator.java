package pe.gob.essalud.apps.validators;

import pe.gob.essalud.apps.dto.auth.request.TokenActivacionRequestDto;
import pe.gob.essalud.apps.model.miessalud.TokenActivacion;

public interface TokenActivacionValidator {

    void validateActivation(TokenActivacionRequestDto request, boolean validUser);
    void validateTokenAndExpiration(TokenActivacion request, String token);

}
