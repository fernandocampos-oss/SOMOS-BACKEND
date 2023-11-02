package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaResponseDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaRequestDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;

import java.util.List;

public interface TareaService extends IcrudService<Tarea> {

    Integer registrarTarea(TareaDTO dto);

    int actualizarTareaAdministrador(String nombreTarea, String plazo, Number idTarea);

    long crearEvidencia(EvidenciaRequestDTO request);

    EvidenciaResponseDTO getEvidenciaPorTarea(Integer idTarea);

    List<Actividad> listarAllPoi();


}
