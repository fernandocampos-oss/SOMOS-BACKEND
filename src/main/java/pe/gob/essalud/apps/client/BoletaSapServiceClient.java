package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForBoletaSapService;
import pe.gob.essalud.apps.dto.pago.response.BoletaPdfSAP;

@BasicAuthForBoletaSapService
@FeignClient(name = "boletasapserviceclient", url = "${feign-clients.boleta-sap-service.url}")
public interface BoletaSapServiceClient {

    @GetMapping("boleta/getByCodigoPlanillaAndMesAndAnio")
    BoletaPdfSAP getBoletaPago(@RequestParam String codigoPlanilla, @RequestParam String anio, @RequestParam String mes);

    @GetMapping("boleta/getPdf")
    ResponseEntity<Resource> getPdf(@RequestParam int idBoleta);

}
