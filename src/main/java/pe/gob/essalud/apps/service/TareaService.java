package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.TareaValidacionTransaccionalDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;
import pe.gob.essalud.apps.service.IcrudService;

import java.util.List;

public interface TareaService extends IcrudService<Tarea> {

    List<Tarea> listarTareaPorRequermientoPersonal(Number idRequerimientoPersonal);

    Integer registrarTareaNoDuplicado(TareaValidacionTransaccionalDTO obj);

    List<Tarea> listarTareaPorPersonal(Number idPersonal);

}
