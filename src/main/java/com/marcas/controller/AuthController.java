package com.marcas.controller;

import com.marcas.base.BaseController;
import com.marcas.dto.auth.UserSessionDto;
import com.marcas.dto.auth.request.AuthUsuarioRegisterRequestDto;
import com.marcas.dto.auth.request.CambiarClaveRequestDto;
import com.marcas.dto.auth.request.GenerarTokenRecuperarClaveRequestDto;
import com.marcas.dto.auth.request.TokenActivacionRequestDto;
import com.marcas.dto.auth.response.AuthUsuarioRegisterResponse;
import com.marcas.dto.auth.response.GenerarTokenRecuperarClaveResponseDto;
import com.marcas.dto.auth.response.LoginResponseDto;
import com.marcas.service.AuthService;
import com.marcas.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthController.AUTH)
@RequiredArgsConstructor
public class AuthController extends BaseController {

    static final String AUTH = "auth";
    private static final String LOGIN = "login";
    private final JwtService jwtService;
    private final AuthService authService;

    @PreAuthorize("authenticated")
    @PostMapping(LOGIN)
    public LoginResponseDto login(@AuthenticationPrincipal User activeUser) {
        String token = getToken(activeUser);
        return new LoginResponseDto(token);
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
        return jwtService.createToken(
                userSession.getId(),
                userSession.getNombres(),
                userSession.getIdSede(),
                userSession.getIdZonaControl(),
                userSession.getIdRol());
    }

}
