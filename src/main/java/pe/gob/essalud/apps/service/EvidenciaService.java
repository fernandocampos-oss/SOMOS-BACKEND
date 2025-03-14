package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;

import java.util.List;

public interface EvidenciaService {

    void registrarEvidenciaExistIndicador(IndicadorExistRequestDto dto);

    void registrarIndicadorExistPrioridad(PrioridadExistRequestDto dto);

    long crearSustentoEvidencia(EvidenciaSustentoRequestDto request);

    EvidenciaResponseDto getEvidenciaById(Integer idEvidencia);

//    List<Evidencia> listEvidenciaByIdIndicador(int idIndicador);

    void modificarEvidencia(int id, UpdateEvidenciaDto request);

    void aprobarEvidencia(ApruebaEvidenciaRequestDto request);

    void eliminarEvidencia(int id);
}
