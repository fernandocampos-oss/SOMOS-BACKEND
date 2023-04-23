package com.marcas.validators;

import com.marcas.dto.auth.request.TokenActivacionRequestDto;
import com.marcas.model.marcaciones.TokenActivacion;

public interface TokenActivacionValidator {

    void validateActivation(TokenActivacionRequestDto request, boolean validUser);
    void validateTokenAndExpiration(TokenActivacion request, String token);

}
