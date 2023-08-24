package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.proyecto.request.ProyectoRequest;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;

import java.util.List;

public interface ProyectoService {

    List<ProyectoRequest> listarProyectos();
    ProyectoRequest guardarProyecto(ProyectoRequest request);
    List<UsuarioNombresResponse> listarUsuariosRed();

}
