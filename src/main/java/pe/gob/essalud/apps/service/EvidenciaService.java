package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadExistRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdateEvidenciaDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaSustentoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorExistRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;

import java.util.List;

public interface EvidenciaService {

    void registrarEvidenciaExistIndicador(IndicadorExistRequestDto dto);

    void registrarIndicadorExistPrioridad(PrioridadExistRequestDto dto);

    long crearSustentoEvidencia(EvidenciaSustentoRequestDto request);

    EvidenciaResponseDto getEvidenciaByTarea(Integer idEvidencia);

    List<Evidencia> listEvidenciaByIdIndicador(int idIndicador);

//    int modificarEvidencia(String nombre, String plazo, Number idEvidencia);

    void modificarEvidencia(int id, UpdateEvidenciaDto request);
}
