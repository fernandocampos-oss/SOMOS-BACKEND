package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.common.annotations.PreAuthorizeAdmin;
import pe.gob.essalud.apps.dto.publicacion.request.PublicacionRequestDto;
import pe.gob.essalud.apps.dto.publicacion.response.PublicacionResponseDto;
import pe.gob.essalud.apps.service.PublicacionService;

import java.util.List;

@RestController
@RequestMapping(PublicacionController.PUBLICACION)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class PublicacionController {

    static final String PUBLICACION = "publicaciones";
    private final PublicacionService publicacionService;

    @GetMapping
    public List<PublicacionResponseDto> listarPublicaciones() {
        return publicacionService.listarPublicaciones();
    }

    @PreAuthorizeAdmin
    @PostMapping
    public long crearPublicacion(@RequestBody PublicacionRequestDto request) {
        return publicacionService.crearPublicacion(request);
    }

    @PreAuthorizeAdmin
    @PutMapping("/{idPublicacion}")
    public void modificarPublicacionDatos(@PathVariable long idPublicacion, @RequestBody PublicacionRequestDto request) {
        publicacionService.modificarPublicacion(idPublicacion, request);
    }

    @PreAuthorizeAdmin
    @DeleteMapping("/{idPublicacion}")
    public void eliminarPublicacion(@PathVariable long idPublicacion) {
        publicacionService.eliminarPublicacion(idPublicacion);
    }

}
