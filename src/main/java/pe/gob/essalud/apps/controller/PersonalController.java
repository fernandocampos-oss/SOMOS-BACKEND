package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.personal.response.PersonalLogeoResponseDto;
import pe.gob.essalud.apps.dto.personal.response.PersonalTokenResponseDto;
import pe.gob.essalud.apps.service.PersonalTokenService;

@RestController
@RequestMapping(PersonalController.PERSONAL)
@RequiredArgsConstructor
public class PersonalController {

    static final String PERSONAL = "personal";
    
    private final PersonalTokenService personalTokenService;

    /**
     * PASO 1: Usuario autenticado en Somos solicita token para ir a Plataforma Integral
     * Requiere estar logueado en Somos EsSalud
     */
    @GetMapping("/generar-token")
    @PreAuthorize("isAuthenticated()")
    public PersonalTokenResponseDto generarToken() {
        return personalTokenService.generarToken();
    }

    /**
     * PASO 2: Plataforma Integral valida el token y obtiene datos del usuario
     * NO requiere autenticación (la Plataforma Integral lo llama)
     */
    @PostMapping("/validar-token")
    public PersonalLogeoResponseDto validarToken(@RequestParam String token) {
        return personalTokenService.validarToken(token);
    }
}
