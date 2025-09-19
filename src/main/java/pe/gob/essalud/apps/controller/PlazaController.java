package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.dto.plaza.response.PlazaResponseDto;
import pe.gob.essalud.apps.service.PlazaService;

@RestController
@RequestMapping(PlazaController.PLAZA)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class PlazaController {

    static final String PLAZA = "plazas";
    private final PlazaService plazaService;

    @GetMapping("/busqueda")
    public PlazaResponseDto buscarPlaza(@RequestParam String plaza, @RequestParam String nombre) {
        return plazaService.buscarPlaza(plaza, nombre);
    }

}
