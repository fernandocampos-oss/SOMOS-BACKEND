package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.common.annotations.PreAuthorizeAdmin;
import pe.gob.essalud.apps.common.annotations.PreAuthorizeAdminCentral;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuariored.request.AdministracionRedUsuariosRequestDto;
import pe.gob.essalud.apps.dto.usuariored.request.UsuarioRedRequest;
import pe.gob.essalud.apps.dto.usuariored.response.AdministracionRedUsuariosResponseDto;
import pe.gob.essalud.apps.dto.usuariored.response.DatosRedesAsignadasResponse;
import pe.gob.essalud.apps.dto.usuariored.response.RedResponse;
import pe.gob.essalud.apps.dto.usuariored.response.UsuarioRedResponse;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;
import pe.gob.essalud.apps.service.UsuarioRedService;

import java.util.List;

@RestController
@RequestMapping(UsuarioRedController.USUARIO_RED)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class UsuarioRedController {

    static final String USUARIO_RED = "usuarios-redes";
    private final UsuarioRedService usuarioRedService;

    @PreAuthorizeAdmin
    @GetMapping("/administradores-red")
    public List<UsuarioNombresResponse> listarAministradoresRed() {
        return usuarioRedService.listarAministradoresRed();
    }

    @PreAuthorizeAdminCentral
    @GetMapping("/redes")
    public List<RedPersonal> listarRedes() {
        return usuarioRedService.listarRedes();
    }

    @PreAuthorizeAdminCentral
    @GetMapping
    public List<UsuarioRedResponse> listarUsuariosRedes() {
        return usuarioRedService.listarUsuariosRedes();
    }

    @PreAuthorizeAdmin
    @GetMapping("/redes-asignadas")
    public List<RedResponse> listarUsuarioRedesAsignadas() {
        return usuarioRedService.listarUsuarioRedesAsignadas();
    }

    @PreAuthorizeAdminCentral
    @PostMapping
    public void asignarRedesUsuario(@RequestBody UsuarioRedRequest request) {
        usuarioRedService.asignarRedesUsuario(request);
    }

    @PreAuthorizeAdminCentral
    @PutMapping
    public void actualizarRedesUsuario(@RequestBody UsuarioRedRequest request) {
        usuarioRedService.actualizarRedesUsuario(request);
    }

    @PreAuthorizeAdminCentral
    @PutMapping("/usuario/{idUsuario}/habilitado/{habilitado}")
    public void habilitarUsuario(@PathVariable long idUsuario, @PathVariable int habilitado){
        usuarioRedService.habilitarUsuario(idUsuario, habilitado == 1);
    }

    @PreAuthorizeAdminCentral
    @PutMapping("/usuario/{idUsuario}/red/{codRed}/habilitado/{habilitado}")
    public void habilitarUsuarioRed(@PathVariable long idUsuario, @PathVariable String codRed,
            @PathVariable int habilitado) {
        usuarioRedService.habilitarUsuarioRed(idUsuario, codRed,habilitado == 1);
    }

    @PreAuthorizeAdminCentral
    @DeleteMapping("/usuario/{idUsuario}/red/{codRed}")
    public void eliminarUsuarioRed(@PathVariable long idUsuario, @PathVariable String codRed) {
        usuarioRedService.eliminarUsuarioRed(idUsuario, codRed);
    }

    @PreAuthorizeAdmin
    @PostMapping("/usuarios-red-asignada")
    public AdministracionRedUsuariosResponseDto obtenerUsuariosRedes(@RequestBody AdministracionRedUsuariosRequestDto request){
        return usuarioRedService.obtenerUsuariosRedes(request);
    }

    @PreAuthorizeAdmin
    @GetMapping("/datos-redes")
    public DatosRedesAsignadasResponse getDatosRedesAsignadas(){ return usuarioRedService.getDatosRedesAsignadas();}
}
