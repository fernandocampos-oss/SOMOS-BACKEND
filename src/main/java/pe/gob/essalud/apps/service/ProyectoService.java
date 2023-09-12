package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.proyecto.request.ProyectoRequest;
import pe.gob.essalud.apps.dto.proyecto.response.BandejaProyectosResponse;
import pe.gob.essalud.apps.dto.usuariored.response.UsuarioDataResponse;

import java.util.List;

public interface ProyectoService {

    List<ProyectoRequest> listarProyectos();
    BandejaProyectosResponse obtenerBandejaProyectos();
    ProyectoRequest guardarProyecto(ProyectoRequest request);
    List<UsuarioDataResponse> listarUsuariosRed();

}
