package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.essalud.apps.dto.cronograma.request.CronogramaPagoDto;
import pe.gob.essalud.apps.dto.cronograma.request.TipoContratoDto;
import pe.gob.essalud.apps.service.CronogramaService;

import java.util.List;

@RestController
@RequestMapping(CronogramaController.CRONOGRAMA)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class CronogramaController {

    static final String CRONOGRAMA = "cronogramas";
    private final CronogramaService cronogramaService;

    @GetMapping("/tipos-contratos")
    public List<TipoContratoDto> listarTiposContratos() {
        return cronogramaService.listarTiposContratos();
    }

    @GetMapping("/pagos")
    public List<CronogramaPagoDto> listarCronogramaPago() {
        return cronogramaService.listarCronogramaPago();
    }

    @PutMapping("/pagos")
    public void actualizarCronogramaPago(@RequestBody List<CronogramaPagoDto> cronogramaPagoDtos) {
        cronogramaService.actualizarCronogramaPago(cronogramaPagoDtos);
    }

}
