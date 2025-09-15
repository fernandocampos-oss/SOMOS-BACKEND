package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForPlazaSapService;
import pe.gob.essalud.apps.dto.plaza.response.PlazaResponseDto;

@BasicAuthForPlazaSapService
@FeignClient(name = "plazasapserviceclient", url = "${feign-clients.plaza-sap-service.url}")
public interface PlazaSapServiceClient {

    @GetMapping("plaza/getPlazaSAP")
    PlazaResponseDto getPlaza(@RequestParam String plaza, @RequestParam String nombre);

}
