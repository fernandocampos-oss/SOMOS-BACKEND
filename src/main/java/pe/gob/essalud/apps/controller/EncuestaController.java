package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.dto.encuesta.response.EncuestaResponseDto;
import pe.gob.essalud.apps.service.EncuestaService;

@RestController
@RequestMapping(EncuestaController.ENCUESTA)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class EncuestaController {

    static final String ENCUESTA = "encuestas";
    private final EncuestaService encuestaService;

    @GetMapping("/encuesta-activa")
    public EncuestaResponseDto obtenerEncuestaActiva() {
        return encuestaService.buscarEncuestaActiva();
    }

}
