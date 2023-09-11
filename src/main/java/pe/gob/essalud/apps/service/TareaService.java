package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaResponseDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaRequestDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

import java.util.List;

public interface TareaService extends IcrudService<Tarea> {

    Integer registrarTarea(TareaDTO dto);

    int actualizarTareaAdministrador(String nombreTarea, String plazo, Number idTarea);

    long crearEvidencia(EvidenciaRequestDTO request);

    List<EvidenciaResponseDTO> listarEvidenciaTarea(Integer idTarea);

}
