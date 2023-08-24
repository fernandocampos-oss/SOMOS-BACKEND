package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.proyecto.request.ProyectoRequest;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.service.ProyectoService;

import java.util.List;

@RestController
@RequestMapping(ProyectoController.PROYECTO)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class ProyectoController {

    static final String PROYECTO = "proyectos";
    private final ProyectoService proyectoService;

    @GetMapping
    public List<ProyectoRequest> listarProyectos() {
        return proyectoService.listarProyectos();
    }

    @GetMapping("/usuarios-red")
    public List<UsuarioNombresResponse> listarUsuariosRed() {
        return proyectoService.listarUsuariosRed();
    }

    @PostMapping
    public ProyectoRequest guardarProyecto(@RequestBody ProyectoRequest request) {
        return proyectoService.guardarProyecto(request);
    }

}
