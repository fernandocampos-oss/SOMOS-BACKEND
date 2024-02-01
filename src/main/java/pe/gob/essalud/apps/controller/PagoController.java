package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.pago.request.PagoBoletaRequestDto;
import pe.gob.essalud.apps.dto.pago.response.BoletaPagoResponseDto;
import pe.gob.essalud.apps.dto.pago.response.PagoHistorialActividadResponseDto;
import pe.gob.essalud.apps.dto.pago.response.TipoBoletaResponseDto;
import pe.gob.essalud.apps.service.PagoService;

import java.util.List;

@RestController
@RequestMapping(PagoController.PAGO)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class PagoController {

    static final String PAGO = "pagos";
    private final PagoService pagoService;

    @GetMapping("/busqueda")
    public BoletaPagoResponseDto buscarBoletaPago(@RequestParam String anio, @RequestParam String mes,
                                                  @RequestParam String tipo) {
        return pagoService.buscarBoletaPago(anio, mes, tipo);
    }

    @GetMapping("/historial/actividades")
    public List<PagoHistorialActividadResponseDto> listarPagosHistorialActividades() {
        return pagoService.listarPagosHistorialActividades();
    }

    @PostMapping("/aceptar-terminos")
    public void aceptarTerminos() {
        pagoService.aceptarTerminos();
    }

    @PostMapping("/accion/{tipoAccion}")
    public void registrarAccion(@PathVariable int tipoAccion, @RequestBody PagoBoletaRequestDto pagoBoletaRequestDto) {
        pagoService.registrarAccion(tipoAccion, pagoBoletaRequestDto);
    }

    @GetMapping("/verificar-terminos")
    public boolean verificarAceptacionTerminos() {
        return pagoService.verificarAceptacionTerminos();
    }

    @GetMapping("/tipos")
    public List<TipoBoletaResponseDto> listarTiposBoletas() {
        return pagoService.listarTiposBoletas();
    }

}
