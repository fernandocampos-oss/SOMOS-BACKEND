package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuariored.request.UsuarioRedRequest;
import pe.gob.essalud.apps.dto.usuariored.response.RedResponse;
import pe.gob.essalud.apps.dto.usuariored.response.UsuarioRedResponse;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;

import java.util.List;

public interface UsuarioRedService {

    List<UsuarioNombresResponse> listarAministradoresRed();
    List<RedPersonal> listarRedes();
    List<UsuarioRedResponse> listarUsuariosRedes();
    List<RedResponse> listarUsuarioRedesAsignadas();
    void asignarRedesUsuario(UsuarioRedRequest request);
    void actualizarRedesUsuario(UsuarioRedRequest request);
    void habilitarUsuario(long idUsuario, boolean habilitado);
    void habilitarUsuarioRed(long idUsuario, String codRed, boolean habilitado);
    void eliminarUsuarioRed(long idUsuario, String codRed);

}
