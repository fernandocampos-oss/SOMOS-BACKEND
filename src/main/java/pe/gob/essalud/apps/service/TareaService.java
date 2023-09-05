package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

public interface TareaService extends IcrudService<Tarea> {

    Integer registrarTarea(TareaDTO dto);

    int actualizarTareaAdministrador(String nombreTarea, String plazo, Number idTarea);



//    List<Tarea> listarTareaPorRequermientoPersonal(Number idRequerimientoPersonal);
//    List<Tarea> listarTareaPorPersonal(Number idPersonal);

}
