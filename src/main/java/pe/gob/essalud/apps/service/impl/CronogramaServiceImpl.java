package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.cronograma.request.CronogramaPagoDto;
import pe.gob.essalud.apps.dto.cronograma.request.TipoContratoDto;
import pe.gob.essalud.apps.model.miessalud.CronogramaPago;
import pe.gob.essalud.apps.repository.miessalud.CronogramaPagoRepository;
import pe.gob.essalud.apps.repository.miessalud.TipoContratoRepository;
import pe.gob.essalud.apps.service.CronogramaService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CronogramaServiceImpl implements CronogramaService {

    private final TipoContratoRepository tipoContratoRepository;
    private final CronogramaPagoRepository cronogramaPagoRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TipoContratoDto> listarTiposContratos() {
        return tipoContratoRepository.findAllByOrderByIdTipoContratoAsc()
                .stream()
                .map(t -> modelMapper.map(t, TipoContratoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CronogramaPagoDto> listarCronogramaPago() {
        List<CronogramaPago> cronogramaPagos = cronogramaPagoRepository.findAllByOrderByTipoContratoIdTipoContratoAscPeriodoPagoIdPeriodoPagoAsc();
        List<CronogramaPagoDto> cronogramaPagoDtos = cronogramaPagos.stream()
                .map(c -> {
                    LocalDate fecha = LocalDate.of(LocalDate.now().getYear(), c.getMes(), c.getDia());
                    int[] tipoPagoAsociados = Arrays.stream(c.getTipoPagoAsociado().split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    CronogramaPagoDto pagoDto = new CronogramaPagoDto();
                    pagoDto.setIdCronogramaPago(c.getIdCronogramaPago());
                    pagoDto.setIdTipoContrato(c.getTipoContrato().getIdTipoContrato());
                    pagoDto.setDescripcionPeriodo(c.getPeriodoPago().getDescripcion());
                    pagoDto.setFecha(fecha);
                    pagoDto.setTipoPagoAsociados(tipoPagoAsociados);
                    return pagoDto;
                })
                .collect(Collectors.toList());
        return cronogramaPagoDtos;
    }

    @Override
    public void actualizarCronogramaPago(List<CronogramaPagoDto> cronogramaPagoDtos) {
        for (CronogramaPagoDto cronogramaPagoDto: cronogramaPagoDtos) {
            CronogramaPago cronogramaPago = cronogramaPagoRepository.findById(cronogramaPagoDto.getIdCronogramaPago()).orElse(null);
            if (cronogramaPago != null) {
                String tipoPagoAsociado = String.join(",", Arrays.stream(cronogramaPagoDto.getTipoPagoAsociados()).mapToObj(String::valueOf).toArray(String[]::new));
                cronogramaPago.setDia(cronogramaPagoDto.getFecha().getDayOfMonth());
                cronogramaPago.setMes(cronogramaPagoDto.getFecha().getMonth().getValue());
                cronogramaPago.setTipoPagoAsociado(tipoPagoAsociado);
                cronogramaPagoRepository.save(cronogramaPago);
            }
        }
    }

}
