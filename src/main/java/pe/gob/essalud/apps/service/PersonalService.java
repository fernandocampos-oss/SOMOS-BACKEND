package pe.gob.essalud.apps.service;

import java.util.List;

import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalFiltroNombreDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Personal;
import pe.gob.essalud.apps.service.IcrudService;

public interface PersonalService extends IcrudService<Personal> {

    List<Personal> listarPersonalPorDependenciaAsignado(Number idDependencia, Number idEstadoPersonal);

    List<Personal> buscarPersonalPorNombre(PersonalFiltroNombreDTO filtro);

    int eliminarPersonalMotivo(Number idEstadoPersonal, String motivo, Number idPersonal);

}
