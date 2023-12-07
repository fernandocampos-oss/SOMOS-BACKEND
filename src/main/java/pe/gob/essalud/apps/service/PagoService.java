package pe.gob.essalud.apps.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import pe.gob.essalud.apps.dto.pago.request.PagoBoletaRequestDto;
import pe.gob.essalud.apps.dto.pago.response.PagoBoletaResponseDto;
import pe.gob.essalud.apps.dto.pago.response.PagoHistorialActividadResponseDto;

import java.util.List;

public interface PagoService {

    List<PagoBoletaResponseDto> listarPagosBoletasBusqueda(int anio, int mes);
    ResponseEntity<Resource> descargarPdfBoleta(int idBoleta);
    String visualizarPdfBoleta(int idBoleta);
    List<PagoHistorialActividadResponseDto> listarPagosHistorialActividades();
    void aceptarTerminos();
    void registrarAccion(int tipoAccion, PagoBoletaRequestDto pagoBoletaRequestDto);
    boolean verificarAceptacionTerminos();

}
