package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.encuesta.request.UsuarioEncuestaRequestDto;
import pe.gob.essalud.apps.dto.encuesta.response.EncuestaResponseDto;
import pe.gob.essalud.apps.dto.encuesta.response.ReporteEncuestaResponseDto;
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

    @PostMapping("/{idEncuesta}/guardar-respuesta")
    public void guardarRespuestaEncuesta(@PathVariable Integer idEncuesta,
             @RequestBody UsuarioEncuestaRequestDto request) {
        encuestaService.guardarRespuesta(idEncuesta, request);
    }

    @GetMapping("/{idEncuesta}/resultado-encuesta")
    public ReporteEncuestaResponseDto obtenerResultadosEncuesta(@PathVariable Integer idEncuesta) {
        return encuestaService.obtenerResultadosEncuesta(idEncuesta);
    }

}
