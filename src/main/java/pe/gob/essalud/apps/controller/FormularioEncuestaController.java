package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.formencuesta.reponse.FormEncuestaResponseDto;
import pe.gob.essalud.apps.dto.formencuesta.request.FormRegisterRespuestaRequestDto;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuestaTrabajador;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormPregunta;
import pe.gob.essalud.apps.service.FormularioEncuestaService;

import java.util.List;

@RestController
@RequestMapping(FormularioEncuestaController.FORMULARIOS_ENCUESTAS)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class FormularioEncuestaController {
    static final String FORMULARIOS_ENCUESTAS = "formularios-encuestas";
    private final FormularioEncuestaService encuestaFormularioService;

    @GetMapping("/listar")
    public List<FormEncuestaResponseDto> listEncuestaByUsuarioCreacion() {
        return encuestaFormularioService.listEncuestaByUsuarioCreacion();
    }

    @GetMapping("/detalle/{id}/encuesta")
    public List<FormPregunta> listarPreguntasByIdEncuesta(@PathVariable("id") Integer id) {
        return encuestaFormularioService.listarPreguntasByIdEncuesta(id);
    }

    @PostMapping("/registrar-respuestas")
    public void registrarRespuesta(@RequestBody FormRegisterRespuestaRequestDto dto) {
        encuestaFormularioService.registrarRespuesta(dto);
    }

    @GetMapping("/evaluar/{id}/finalizado")
    public boolean evaluarEncuestaFinalizado(@PathVariable("id") Integer idEncuesta) {
        return encuestaFormularioService.evaluarEncuestaFinalizado(idEncuesta);
    }
}
