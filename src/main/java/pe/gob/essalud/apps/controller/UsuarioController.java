package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.base.BaseController;
import pe.gob.essalud.apps.common.annotations.PreAuthorizeAdminCentral;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioActualizarDatosRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarCorreoRequestDto;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping(UsuarioController.USERS)
@RequiredArgsConstructor
@PreAuthorize("authenticated")
public class UsuarioController extends BaseController {

    static final String USERS = "usuarios";

    private final UsuarioService usuarioService;

    /*
    @GetMapping
    public List<UsuarioResponseDto> search() {
        return usuarioService.search();
    }

    @GetMapping("nombres")
    public List<UsuarioNombresResponse> nombres(@RequestParam boolean mostrarTodos) {
        return usuarioService.getNombres(mostrarTodos);
    }
    */

    @GetMapping("{id}")
    public UsuarioResponseDto get(@PathVariable long id) {
        return usuarioService.get(id);
    }

    @GetMapping("find/{id}")
    public UsuarioResponseDto find(@PathVariable long id) {
        return usuarioService.find(id);
    }

    @PreAuthorizeAdminCentral
    @GetMapping("find/numero-documento/{numeroDocumento}")
    public UsuarioResponseDto findByNumeroDocumento(@PathVariable String numeroDocumento) {
        return usuarioService.findByNumeroDocumento(numeroDocumento);
    }

    /*
    @PreAuthorizeAdmin
    @PostMapping
    public long save(@RequestBody UsuarioRegisterUpdateRequestDto model) {
        return usuarioService.save(model);
    }

    @PreAuthorizeAdmin
    @PutMapping("{id}")
    public void update(@PathVariable long id, @RequestBody UsuarioRegisterUpdateRequestDto model) {
        usuarioService.update(id, model);
    }

    @PreAuthorizeAdmin
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        usuarioService.delete(id);
    }
    */

    @PutMapping("{id}/cambiar-clave")
    public void cambiarClave(@PathVariable long id, @RequestBody UsuarioCambiarClaveRequestDto request) {
        usuarioService.cambiarClave(id, request);
    }

    @PutMapping("{id}/actualizar-datos")
    public void actualizarDatos(@PathVariable long id, @RequestBody UsuarioActualizarDatosRequestDto request) {
        usuarioService.actualizarDatos(id, request);
    }

    @PreAuthorizeAdminCentral
    @PutMapping("{id}/actualizar-datos-administrador")
    public void actualizarDatosAdministrador(@PathVariable long id, @RequestBody UsuarioActualizarDatosRequestDto request) {
        usuarioService.actualizarDatosAdministrador(id, request);
    }

    @PutMapping("{id}/cambiar-correo")
    public void cambiarCorreo(@PathVariable long id, @RequestBody UsuarioCambiarCorreoRequestDto request) {
        usuarioService.cambiarCorreo(id, request);
    }
    @GetMapping("/integration/nombres")
    public List<Usuario> integrationFindByNombresActivo(@RequestParam("nombres") String nombres) {
        return usuarioService.integrationFindByNombresActivo(nombres);
    }

    @GetMapping("/permiso/modulo/{modulo}")
    public boolean usuarioTienePermisoModulo(@PathVariable String modulo) {
        return usuarioService.usuarioTienePermisoModulo(modulo);
    }

}