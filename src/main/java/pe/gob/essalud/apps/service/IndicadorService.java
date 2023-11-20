package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.PendienteDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.util.List;

public interface IndicadorService {

    void registrarIndicador(IndicadorRequestDto requestDto);

    List<PendienteDto> listPendientesTrabajadorByUser();

    List<TipoValorMeta> getAllTipoValorMeta();

    void modificarIndicador(Integer idIndicador, Indicador request);

    int asignarPesoIndicador(int peso, int idIndicador);

    //    List<Indicador> getListIndicadoresFinalizadoByUser();

}
