package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.TareaRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

import java.util.List;
import java.util.Optional;

public interface TareaService {

    Integer registrarTarea(TareaRequestDto dto);

    int actualizarTareaAdministrador(String nombre, String plazo, Number idTarea);

    long crearEvidenciaTarea(EvidenciaRequestDto request);

    EvidenciaResponseDto getEvidenciaByTarea(Integer idTarea);

    List<Tarea> getTareasByIdIndicador(int idIndicador);

    ////    int aprobarIndicador(Number estado, Number idIndicadorUsuario);

////    int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario);

}
