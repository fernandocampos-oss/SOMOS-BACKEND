package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.eleccion.request.VotoRequestDto;
import pe.gob.essalud.apps.dto.eleccion.response.EleccionResponseDto;
import pe.gob.essalud.apps.service.EleccionService;

@RestController
@RequestMapping(EleccionController.ELECCION)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class EleccionController {

    static final String ELECCION = "elecciones";
    private final EleccionService eleccionService;

    @GetMapping("/eleccion-activa")
    public EleccionResponseDto obtenerEleccionActiva() {
        return eleccionService.buscarEleccionActiva();
    }

    @PostMapping("/registrar-voto")
    public void registrarVoto(@RequestBody VotoRequestDto votoRequestDto) {
        eleccionService.guardarVoto(votoRequestDto);
    }

}
