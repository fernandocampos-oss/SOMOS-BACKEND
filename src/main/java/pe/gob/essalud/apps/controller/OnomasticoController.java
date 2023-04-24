package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.base.BaseController;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.service.OnomasticoService;

import java.util.List;

@RestController
@RequestMapping(OnomasticoController.ONOMASTICO)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class OnomasticoController extends BaseController {

    static final String ONOMASTICO = "onomasticos";
    private final OnomasticoService onomasticoService;

    @GetMapping
    public List<Onomastico> findAllOnomasticos() {
        return onomasticoService.findAllOnomasticos();
    }

    @GetMapping("mes/{mes}")
    public List<Onomastico> findAllOnomasticosByMes(@PathVariable String mes) {
        return onomasticoService.findAllOnomasticosByMes(mes);
    }

    @GetMapping("mes/{mes}/dia/{dia}")
    public List<Onomastico> findAllOnomasticosByMesAndDia(@PathVariable String mes, @PathVariable String dia) {
        return onomasticoService.findAllOnomasticosByMesAndDia(mes, dia);
    }

}
