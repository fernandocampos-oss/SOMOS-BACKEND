package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import pe.gob.essalud.apps.dto.auth.request.AuthUsuarioRegisterRequestDto;
import pe.gob.essalud.apps.dto.auth.request.CambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.auth.request.GenerarTokenRecuperarClaveRequestDto;
import pe.gob.essalud.apps.dto.auth.request.TokenActivacionRequestDto;
import pe.gob.essalud.apps.dto.auth.response.AuthUsuarioRegisterResponse;
import pe.gob.essalud.apps.dto.auth.response.GenerarTokenRecuperarClaveResponseDto;

public interface AuthService {

    UserSessionDto getUserSession();

    int getIdUserSession();

    boolean hasRole(int idRole);

    boolean hasAdditionalRole(int idRole);

    String getCodRedSession();

    String getCodUnidadSession();

    UserSessionDto findByUsername(String username);

    AuthUsuarioRegisterResponse save(AuthUsuarioRegisterRequestDto model);

    GenerarTokenRecuperarClaveResponseDto generarTokenRecuperarClave(GenerarTokenRecuperarClaveRequestDto request);

    void activarToken(TokenActivacionRequestDto request, Boolean validateUser);

    void cambiarClave(CambiarClaveRequestDto request);

}
