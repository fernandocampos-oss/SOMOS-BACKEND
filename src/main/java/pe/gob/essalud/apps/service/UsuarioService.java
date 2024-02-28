package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarCorreoRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioRegisterUpdateRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioActualizarDatosRequestDto;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDto> search();

    UsuarioResponseDto get(long id);

    UsuarioResponseDto find(long id);

    void update(long id, UsuarioRegisterUpdateRequestDto model);

    void delete(long id);

    long save(UsuarioRegisterUpdateRequestDto model);

    List<UsuarioNombresResponse> getNombres(boolean mostrarTodos);

    void cambiarClave(long id, UsuarioCambiarClaveRequestDto request);

    void updateDatosSAP(long id);

    void actualizarDatos(long id, UsuarioActualizarDatosRequestDto request);

    void cambiarCorreo(long id, UsuarioCambiarCorreoRequestDto request);

}
