package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoPersonal;
import pe.gob.essalud.apps.service.IcrudService;

import java.util.List;

public interface RequerimientoPersonalService extends IcrudService<RequerimientoPersonal> {

    List<RequerimientoPersonal> listarRequerimientosPorPersonal(Number idPersonal);

    List<RequerimientoPersonal> validarDuplicadoRequerimientoPersonal(Number idRequerimiento, Number idPersonal);

}
