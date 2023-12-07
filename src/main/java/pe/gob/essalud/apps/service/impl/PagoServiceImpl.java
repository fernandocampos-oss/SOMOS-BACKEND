package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.BoletaSapServiceClient;
import pe.gob.essalud.apps.dto.pago.request.PagoBoletaRequestDto;
import pe.gob.essalud.apps.dto.pago.response.PagoBoletaResponseDto;
import pe.gob.essalud.apps.dto.pago.response.PagoHistorialActividadResponseDto;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.PagoHistorialActividad;
import pe.gob.essalud.apps.repository.miessalud.PagoHistorialActividadRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PagoService;
import pe.gob.essalud.apps.service.UsuarioService;

import java.io.IOException;
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
    public List<PagoBoletaResponseDto> listarPagosBoletasBusqueda(int anio, int mes) {
        UsuarioResponseDto usuario = usuarioService.get(authService.getIdUserSession());
        return boletaSapServiceClient.getBoletaPago(usuario.getCodigoPlanilla(), anio, mes);
    }

    @Override
    public ResponseEntity<Resource> descargarPdfBoleta(int idBoleta) {
        return boletaSapServiceClient.getPdf(idBoleta);
    }

    @Override
    public String visualizarPdfBoleta(int idBoleta) {
        String pdfBase64 = "";
        ResponseEntity<Resource> response = boletaSapServiceClient.getPdf(idBoleta);
        try {
            byte[] pdfBytes = IOUtils.toByteArray(response.getBody().getInputStream());
            pdfBase64 = java.util.Base64.getEncoder().encodeToString(pdfBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pdfBase64;
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
