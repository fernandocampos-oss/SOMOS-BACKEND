package pe.gob.essalud.apps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.essalud.apps.dto.material.MaestroMaterialSAP;
import pe.gob.essalud.apps.dto.material.StockMaterialSAP;
import pe.gob.essalud.apps.service.MaterialService;

import java.util.List;

@RestController
@RequestMapping(MaterialController.MATERIAL)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class MaterialController {

    static final String MATERIAL = "materiales";
    private final MaterialService materialService;

    @GetMapping("/maestro")
    public List<MaestroMaterialSAP> buscarMaestroMaterial(@RequestParam String codigoMaterial) {
        return materialService.buscarMaestroMaterial(codigoMaterial);
    }

    @GetMapping("/stock")
    public List<StockMaterialSAP> buscarStockMaterial(@RequestParam String codigoMaterial, @RequestParam String codigoRed) {
        return materialService.buscarStockMaterial(codigoMaterial, codigoRed);
    }

}
