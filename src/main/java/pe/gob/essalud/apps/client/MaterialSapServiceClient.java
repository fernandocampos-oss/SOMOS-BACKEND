package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForMaterialSapService;
import pe.gob.essalud.apps.dto.material.MaestroMaterialSAP;
import pe.gob.essalud.apps.dto.material.StockMaterialSAP;

import java.util.List;

@BasicAuthForMaterialSapService
@FeignClient(name = "materialsapserviceclient", url = "${feign-clients.material-sap-service.url}")
public interface MaterialSapServiceClient {

    @GetMapping("material/getMaestroMaterialSAP")
    List<MaestroMaterialSAP> getMaestroMaterialSAP(@RequestParam String nombreMaterial);

    @GetMapping("material/getStockMaterialSAP")
    List<StockMaterialSAP> getStockMaterialSAP(@RequestParam String codigoMaterial, @RequestParam String codigoRed);

}
