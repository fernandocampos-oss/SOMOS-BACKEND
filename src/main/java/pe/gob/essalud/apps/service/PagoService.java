package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.pago.request.PagoBoletaRequestDto;
import pe.gob.essalud.apps.dto.pago.response.PagoBoletaResponseDto;
import pe.gob.essalud.apps.dto.pago.response.PagoHistorialActividadResponseDto;

import java.util.List;

public interface PagoService {

    List<PagoBoletaResponseDto> listarPagosBoletasBusqueda();
    List<PagoHistorialActividadResponseDto> listarPagosHistorialActividades();
    void aceptarTerminos();
    void registrarAccion(int tipoAccion, PagoBoletaRequestDto pagoBoletaRequestDto);
    boolean verificarAceptacionTerminos();

}
