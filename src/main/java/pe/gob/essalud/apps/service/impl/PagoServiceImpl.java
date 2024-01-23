package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.BoletaSapServiceClient;
import pe.gob.essalud.apps.dto.pago.request.PagoBoletaRequestDto;
import pe.gob.essalud.apps.dto.pago.response.*;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.PagoHistorialActividad;
import pe.gob.essalud.apps.repository.miessalud.PagoHistorialActividadRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PagoService;
import pe.gob.essalud.apps.service.UsuarioService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;
    private final BoletaSapServiceClient boletaSapServiceClient;

    @Override
    public PagoBoletaResponseDto buscarPagosBoleta(String anio, String mes) {
        PagoBoletaResponseDto pagoBoletaResponseDto = new PagoBoletaResponseDto();
        UsuarioResponseDto usuario = usuarioService.find(authService.getIdUserSession());
        BoletaPdfSAP boletaPdfSAP = boletaSapServiceClient.getBoletaPago(usuario.getCodigoPlanilla(), anio, mes);
        if (boletaPdfSAP != null) {
            if (boletaPdfSAP.getBoleta() != null) {
                BoletaSAP boletaSAP = boletaPdfSAP.getBoleta().stream()
                        .filter(b -> b.getCodigoPlanilla().equals(usuario.getCodigoPlanilla())).findFirst().orElse(null);
                pagoBoletaResponseDto = modelMapper.map(boletaSAP, PagoBoletaResponseDto.class);
            }
            if (boletaPdfSAP.getPdf() != null) {
                String pdfBase64 = boletaPdfSAP.getPdf().stream().map(PdfSAP::getLineaPdfBase64).collect(Collectors.joining());
                pagoBoletaResponseDto.setPdfBase64(pdfBase64);
            }
        }
        return pagoBoletaResponseDto;
    }

    @Override
    public List<PagoHistorialActividadResponseDto> listarPagosHistorialActividades() {
        return pagoHistorialActividadRepository.findByUsuarioCreacionOrderByIdPagoHistorial(authService.getIdUserSession())
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

}
