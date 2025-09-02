package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarCorreoRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioRegisterUpdateRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioActualizarDatosRequestDto;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDto> search();

    UsuarioResponseDto get(long id);

    UsuarioResponseDto find(long id);

    UsuarioResponseDto findByNumeroDocumento(String numeroDocumento);

    void update(long id, UsuarioRegisterUpdateRequestDto model);

    void delete(long id);

    long save(UsuarioRegisterUpdateRequestDto model);

    List<UsuarioNombresResponse> getNombres(boolean mostrarTodos);

    void cambiarClave(long id, UsuarioCambiarClaveRequestDto request);

    void updateDatosSAP(long id);

    void actualizarDatos(long id, UsuarioActualizarDatosRequestDto request);

    void actualizarDatosAdministrador(long id, UsuarioActualizarDatosRequestDto request);

    void cambiarCorreo(long id, UsuarioCambiarCorreoRequestDto request);

    List<Usuario> integrationFindByNombresActivo(String nombres);

    boolean usuarioTienePermisoModulo(String modulo);
}
