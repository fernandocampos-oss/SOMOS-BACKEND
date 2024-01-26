package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.pago.request.PagoBoletaRequestDto;
import pe.gob.essalud.apps.dto.pago.response.BoletaPagoResponseDto;
import pe.gob.essalud.apps.dto.pago.response.PagoHistorialActividadResponseDto;
import pe.gob.essalud.apps.dto.pago.response.TipoBoletaResponseDto;

import java.util.List;

public interface PagoService {

    BoletaPagoResponseDto buscarBoletaPago(String anio, String mes, String tipo);
    List<PagoHistorialActividadResponseDto> listarPagosHistorialActividades();
    void aceptarTerminos();
    void registrarAccion(int tipoAccion, PagoBoletaRequestDto pagoBoletaRequestDto);
    boolean verificarAceptacionTerminos();

    List<TipoBoletaResponseDto> listarTiposBoletas();

}
