package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.encuesta.request.UsuarioEncuestaRequestDto;
import pe.gob.essalud.apps.dto.encuesta.response.EncuestaResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.ReporteInscritosDto;
import pe.gob.essalud.apps.dto.inscripcion.response.UsuariosInscritosResponseDto;
import pe.gob.essalud.apps.model.miessalud.Inscripcion;
import pe.gob.essalud.apps.service.InscripcionService;

import java.util.List;

@RestController
@RequestMapping(InscripcionController.INSCRIPCION)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class InscripcionController {

    static final String INSCRIPCION = "inscripciones";
    private final InscripcionService inscripcionService;


    @GetMapping("{id}")
    public InscripcionResponseDto obtenerInscripcion(@PathVariable int id) {
        return inscripcionService.buscarInscripcionPorId(id);
    }

    @PostMapping("/{idInscripcion}/guardar-respuesta")
    public void guardarRespuestaEncuesta(@PathVariable Integer idInscripcion) {
        inscripcionService.guardarInscripcion(idInscripcion);
    }

    @GetMapping("/{idInscripcion}/inscritos")
    public ReporteInscritosDto getUsuariosInscritos(@PathVariable Integer idInscripcion) {
        return inscripcionService.getUsuariosInscritos(idInscripcion);
    }
}
