package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadExistRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaSustentoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorExistRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;

import java.util.List;

public interface EvidenciaService {

//    Integer registrarTarea(TareaRequestDto dto);

    void registrarEvidenciaExistIndicador(IndicadorExistRequestDto dto);

    void registrarIndicadorExistPrioridad(PrioridadExistRequestDto dto);

    int actualizarTareaAdministrador(String nombre, String plazo, Number idTarea);

    long crearSustentoEvidencia(EvidenciaSustentoRequestDto request);

    EvidenciaResponseDto getEvidenciaByTarea(Integer idTarea);

    List<Evidencia> listEvidenciaByIdIndicador(int idIndicador);

    ////    int aprobarIndicador(Number estado, Number idIndicadorUsuario);

////    int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario);

}
