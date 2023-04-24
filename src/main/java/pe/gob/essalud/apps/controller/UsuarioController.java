package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.base.BaseController;
import pe.gob.essalud.apps.common.annotations.PreAuthorizeAdmin;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioRegisterUpdateRequestDto;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;
import pe.gob.essalud.apps.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping(UsuarioController.USERS)
@RequiredArgsConstructor
@PreAuthorizeAdmin
public class UsuarioController extends BaseController {

    static final String USERS = "usuarios";

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponseDto> search() {
        return usuarioService.search();
    }

    @GetMapping("nombres")
    public List<UsuarioNombresResponse> nombres(@RequestParam boolean mostrarTodos) {
        return usuarioService.getNombres(mostrarTodos);
    }

    @GetMapping("{id}")
    public UsuarioResponseDto get(@PathVariable long id) {
        return usuarioService.get(id);
    }

    @PostMapping
    public long save(@RequestBody UsuarioRegisterUpdateRequestDto model) {
        return usuarioService.save(model);
    }
    @PutMapping("{id}")
    public void update(@PathVariable long id, @RequestBody UsuarioRegisterUpdateRequestDto model) {
        usuarioService.update(id, model);
    }
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        usuarioService.delete(id);
    }

}