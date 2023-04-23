package com.marcas.service;

import com.marcas.dto.auth.UserSessionDto;
import com.marcas.dto.auth.request.AuthUsuarioRegisterRequestDto;
import com.marcas.dto.auth.request.CambiarClaveRequestDto;
import com.marcas.dto.auth.request.GenerarTokenRecuperarClaveRequestDto;
import com.marcas.dto.auth.request.TokenActivacionRequestDto;
import com.marcas.dto.auth.response.AuthUsuarioRegisterResponse;
import com.marcas.dto.auth.response.GenerarTokenRecuperarClaveResponseDto;

public interface AuthService {

    UserSessionDto getUserSession();

    int getIdUserSession();

    boolean hasRole(int idRole);

    int getIdSedeSession();

    Integer getIdZonaControlSession();

    UserSessionDto findByUsername(String username);

    AuthUsuarioRegisterResponse save(AuthUsuarioRegisterRequestDto model);

    GenerarTokenRecuperarClaveResponseDto generarTokenRecuperarClave(GenerarTokenRecuperarClaveRequestDto request);

    void activarToken(TokenActivacionRequestDto request, Boolean validateUser);

    void cambiarClave(CambiarClaveRequestDto request);

}
