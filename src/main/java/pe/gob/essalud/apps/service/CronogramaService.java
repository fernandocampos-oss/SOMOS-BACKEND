package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.cronograma.request.CronogramaPagoDto;
import pe.gob.essalud.apps.dto.cronograma.request.TipoContratoDto;

import java.util.List;

public interface CronogramaService {

    List<TipoContratoDto> listarTiposContratos();
    List<CronogramaPagoDto> listarCronogramaPago();
    void actualizarCronogramaPago(List<CronogramaPagoDto> cronogramaPagoDtos);

}
