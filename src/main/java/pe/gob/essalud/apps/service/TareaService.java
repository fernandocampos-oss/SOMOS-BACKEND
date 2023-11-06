package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.TareaRequestDto;

public interface TareaService {

    Integer registrarTarea(TareaRequestDto dto);

    int actualizarTareaAdministrador(String nombre, String plazo, Number idTarea);

    long crearEvidenciaTarea(EvidenciaRequestDto request);

    EvidenciaResponseDto getEvidenciaByTarea(Integer idTarea);

}
