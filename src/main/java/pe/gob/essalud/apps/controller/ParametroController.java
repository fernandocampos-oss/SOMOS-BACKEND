package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.model.miessalud.GdrParametro;
import pe.gob.essalud.apps.service.ParametroService;

@RestController
@RequestMapping(ParametroController.PARAMETRO)
@RequiredArgsConstructor
public class ParametroController {

    static final String PARAMETRO = "parametros";
    private final ParametroService parametroService;

    @GetMapping("/listar")
    public GdrParametro obtenerParametros() {
        return parametroService.obtenerParametros();
    }

    @PutMapping("/modificar/{id}")
    public void actualizarParametros(@PathVariable Integer id, @RequestBody GdrParametro request) {
        parametroService.actualizarParametros(id, request);
    }

}
