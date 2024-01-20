package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelTrabajadorDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.PendienteDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.util.List;
import java.util.Optional;

public interface IndicadorService {

    void registrarIndicador(IndicadorRequestDto requestDto);

    List<PendienteDto> listPendientesTrabajadorByUser();

    List<TipoValorMeta> getAllTipoValorMeta();

    void modificarIndicador(Integer idIndicador, Indicador request);

    int asignarPesoIndicador(int peso, int idIndicador);

    ExcelTrabajadorDto generarExcelTrabajador();

    Optional<Integer> sumaTotalPesoAllIndicadorByTrabajador(int idVotante);

}
