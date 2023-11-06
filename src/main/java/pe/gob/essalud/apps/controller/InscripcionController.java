package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.inscripcion.request.InscripcionRequestDto;
import pe.gob.essalud.apps.dto.inscripcion.request.InscripcionVotoRequestDto;
import pe.gob.essalud.apps.dto.inscripcion.response.*;
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

    @GetMapping("/datos/{id}")
    public InscripcionDatosResponseDto obtenerDatosInscripcion(@PathVariable int id) {
        return inscripcionService.buscarDatosInscripcionPorId(id);
    }

    @PostMapping("/guardar-respuesta")
    public void registrarInscripcion(@RequestBody InscripcionRequestDto request) {
        inscripcionService.guardarInscripcion(request);
    }

    @GetMapping("/{idInscripcion}/inscritos")
    public ReporteInscritosDto getUsuariosInscritos(@PathVariable Integer idInscripcion) {
        return inscripcionService.getUsuariosInscritos(idInscripcion);
    }

    @GetMapping("/votaciones")
    public List<InscripcionVotacionResponseDto> listarVotaciones() {
        return inscripcionService.listarVotacionesActivas();
    }

    @PostMapping("/registrar-voto")
    public void registrarVoto(@RequestBody InscripcionVotoRequestDto votoRequestDto) {
        inscripcionService.guardarVoto(votoRequestDto);
    }

    @PutMapping("/{idInscripcion}/activar-votacion/{votoActivo}")
    public void activarVotacion(@PathVariable int idInscripcion, @PathVariable boolean votoActivo) {
        inscripcionService.activarVotacion(idInscripcion, votoActivo);
    }

    @GetMapping("/asignaciones")
    public List<InscripcionAsignadaResponseDto> getInscripcionesAsignadas(){
        return inscripcionService.inscripcionesAsignadas();
    }
}
