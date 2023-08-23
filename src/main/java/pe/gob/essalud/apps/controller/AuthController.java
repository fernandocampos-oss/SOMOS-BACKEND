package pe.gob.essalud.apps.controller;

import pe.gob.essalud.apps.base.BaseController;
import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import pe.gob.essalud.apps.dto.auth.request.AuthUsuarioRegisterRequestDto;
import pe.gob.essalud.apps.dto.auth.request.CambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.auth.request.GenerarTokenRecuperarClaveRequestDto;
import pe.gob.essalud.apps.dto.auth.request.TokenActivacionRequestDto;
import pe.gob.essalud.apps.dto.auth.response.AuthUsuarioRegisterResponse;
import pe.gob.essalud.apps.dto.auth.response.GenerarTokenRecuperarClaveResponseDto;
import pe.gob.essalud.apps.dto.auth.response.LoginResponseDto;
import pe.gob.essalud.apps.dto.auth.response.UserResponseDto;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.service.UsuarioService;

@RestController
@RequestMapping(AuthController.AUTH)
@RequiredArgsConstructor
public class AuthController extends BaseController {

    static final String AUTH = "auth";
    private static final String LOGIN = "login";
    private final JwtService jwtService;
    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PreAuthorize("authenticated")
    @PostMapping(LOGIN)
    public LoginResponseDto login(@AuthenticationPrincipal User activeUser) {
        String token = getToken(activeUser);
        UserResponseDto user = new UserResponseDto(activeUser.getUsername(), activeUser.getAuthorities());
        return new LoginResponseDto(user, token);
    }

    @PostMapping
    public AuthUsuarioRegisterResponse save(@RequestBody AuthUsuarioRegisterRequestDto model) {
        return authService.save(model);
    }

    @PostMapping("generar-token-recuperar-clave")
    public GenerarTokenRecuperarClaveResponseDto generarTokenRecuperarClave(
            @RequestBody GenerarTokenRecuperarClaveRequestDto model) {
        return authService.generarTokenRecuperarClave(model);
    }

    @PutMapping("cambiar-clave")
    public void cambiarClave(@RequestBody CambiarClaveRequestDto request) {
        authService.cambiarClave(request);
    }

    @PutMapping("token/activar")
    public void activarToken(@RequestBody TokenActivacionRequestDto model) {
        authService.activarToken(model, true);
    }

    private String getToken(User activeUser) {
        UserSessionDto userSession = authService.findByUsername(activeUser.getUsername());
        usuarioService.updateDatosSAP(userSession.getId());
        return jwtService.createToken(
                userSession.getId(),
                userSession.getNombres(),
                userSession.getIdRol(),
                userSession.getCodRed(),
                userSession.getCodUnidad(),
                userSession.getIdRolAdicional());
    }

}
