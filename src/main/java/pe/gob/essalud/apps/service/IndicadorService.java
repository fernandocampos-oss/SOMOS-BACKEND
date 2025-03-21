package pe.gob.essalud.apps.service;

import org.springframework.core.io.ByteArrayResource;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelTrabajadorDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.PendienteDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IndicadorService {

    void registrarIndicador(IndicadorRequestDto requestDto);

    List<PendienteDto> listPendientesTrabajadorByUser();

    List<PendienteDto> listPendientesTrabajadorByVotanteAdmin(int idVotante);

    List<TipoValorMeta> getAllTipoValorMeta();

    ExcelTrabajadorDto generarExcelTrabajador();

    ExcelTrabajadorDto generarExcelTrabajadorByVotanteAdmin(int idVotante);

    ByteArrayResource generateExcel(ExcelTrabajadorDto excelTrabajadorDto) throws IOException;

    Optional<Integer> sumaTotalPesoAllIndicadorByTrabajador(int idVotante);

    void modificarIndicador(int id, Indicador request);

    void eliminarIndicador(int id);

}
