package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.BoletaSapServiceClient;
import pe.gob.essalud.apps.dto.pago.request.PagoBoletaRequestDto;
import pe.gob.essalud.apps.dto.pago.response.*;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.*;
import pe.gob.essalud.apps.repository.miessalud.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PagoService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private static final int TIPO_ACCION_APROBACION_TERMINOS = 1;
    private static final int TIPO_ACCION_VISUALIZACION_BOLETA = 2;
    private static final int TIPO_ACCION_DESCARGA_BOLETA = 3;

    private final PagoHistorialActividadRepository pagoHistorialActividadRepository;
    private final TipoBoletaRepository tipoBoletaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoContratoRepository tipoContratoRepository;
    private final CronogramaPagoRepository cronogramaPagoRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final BoletaSapServiceClient boletaSapServiceClient;

    @Override
    public BoletaPagoResponseDto buscarBoletaPago(String anio, String mes, String tipo) {
        Usuario usuario = usuarioRepository.findById((long) authService.getIdUserSession()).orElseThrow(() -> new ValidationException("El usuario no existe"));
        if (validarConsultaSegunCronograma(usuario, anio, mes, tipo)) {
            return boletaSapServiceClient.getBoletaPago(usuario.getCodigoPlanilla(), anio, mes, tipo);
        }
        return new BoletaPagoResponseDto();
    }

    @Override
    public List<PagoHistorialActividadResponseDto> listarPagosHistorialActividades() {
        return pagoHistorialActividadRepository.findByUsuarioCreacionOrderByIdPagoHistorialDesc(authService.getIdUserSession())
                .stream()
                .map(p -> modelMapper.map(p, PagoHistorialActividadResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void aceptarTerminos() {
        if (!verificarAceptacionTerminos()) {
            PagoHistorialActividad pagoHistorialActividad = new PagoHistorialActividad();
            pagoHistorialActividad.setAccion("Aprobación de términos y condiciones");
            pagoHistorialActividad.setTipoAccion(TIPO_ACCION_APROBACION_TERMINOS);
            pagoHistorialActividad.setDetalle("Consentimiento de boleta de pago digitalmente");
            pagoHistorialActividad.setTipoBoleta("");
            pagoHistorialActividad.setUsuarioCreacion(authService.getIdUserSession());
            pagoHistorialActividadRepository.save(pagoHistorialActividad);
        }
    }

    @Override
    public void registrarAccion(int tipoAccion, PagoBoletaRequestDto pagoBoletaRequestDto) {
        if (tipoAccion != TIPO_ACCION_VISUALIZACION_BOLETA && tipoAccion != TIPO_ACCION_DESCARGA_BOLETA ) {
            throw new ValidationException("Tipo de acción inválida");
        }

        String accion = "Visualiza Boleta";
        if (tipoAccion != TIPO_ACCION_VISUALIZACION_BOLETA) {
            accion = "Descarga Boleta";
        }

        PagoHistorialActividad pagoHistorialActividad = new PagoHistorialActividad();
        pagoHistorialActividad.setAccion(accion);
        pagoHistorialActividad.setTipoAccion(tipoAccion);
        pagoHistorialActividad.setDetalle(pagoBoletaRequestDto.getDetalle());
        pagoHistorialActividad.setTipoBoleta(pagoBoletaRequestDto.getTipoBoleta());
        pagoHistorialActividad.setUsuarioCreacion(authService.getIdUserSession());
        pagoHistorialActividadRepository.save(pagoHistorialActividad);
    }

    @Override
    public boolean verificarAceptacionTerminos() {
        Optional<PagoHistorialActividad> pagoHistorialActividad =
                pagoHistorialActividadRepository.findByTipoAccionAndUsuarioCreacion(TIPO_ACCION_APROBACION_TERMINOS, authService.getIdUserSession());
        return pagoHistorialActividad.isPresent();
    }

    @Override
    public List<TipoBoletaResponseDto> listarTiposBoletas() {
        return tipoBoletaRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TipoBoletaResponseDto.class))
                .collect(Collectors.toList());
    }

    private boolean validarConsultaSegunCronograma(Usuario usuario, String anio, String mes, String tipo) {
        boolean consultaPermitida = true;
        LocalDate fechaActual = LocalDate.now(ZoneId.of("America/Lima"));
        int anioConsulta = Integer.parseInt(anio);
        if (anioConsulta == fechaActual.getYear() && usuario.getRegimen() != null) {
            List<TipoContrato> tipoContratos = tipoContratoRepository.findAllByOrderByIdTipoContratoAsc();
            TipoContrato tipoContratoEncontrado = null;

            bucleExterior:
            for (TipoContrato tipoContrato: tipoContratos) {
                for (String codigo: tipoContrato.getCodigo().split(",")) {
                    if (usuario.getRegimen().toUpperCase().contains(codigo.toUpperCase())) {
                        tipoContratoEncontrado = tipoContrato;
                        break bucleExterior;
                    }
                }
            }

            if (tipoContratoEncontrado != null) {
                TipoBoleta tipoBoleta = tipoBoletaRepository.findFirstByTipo(tipo);
                if (tipoBoleta != null) {
                    Integer idTipoContrato = tipoContratoEncontrado.getIdTipoContrato();
                    int mesConsulta = Integer.parseInt(mes);
                    List<CronogramaPago> cronogramaPagos = cronogramaPagoRepository.findAllByOrderByTipoContratoIdTipoContratoAscPeriodoPagoIdPeriodoPagoAsc()
                            .stream()
                            .filter(c -> c.getTipoContrato().getIdTipoContrato().equals(idTipoContrato))
                            .collect(Collectors.toList());
                    for (CronogramaPago cronogramaPago: cronogramaPagos) {
                        List<String> tiposPagosList = Arrays.asList(cronogramaPago.getTipoPagoAsociado().split(","));
                        if (mesConsulta == cronogramaPago.getMes() && fechaActual.getDayOfMonth() < cronogramaPago.getDia() &&
                                tiposPagosList.contains(String.valueOf(tipoBoleta.getIdTipoBoleta()))) {
                            consultaPermitida = false;
                            break;
                        }
                    }
                }
            }
        }
        return consultaPermitida;
    }

}
