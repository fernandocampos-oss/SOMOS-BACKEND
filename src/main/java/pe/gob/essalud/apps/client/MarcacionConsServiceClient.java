package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForMarcacionConsService;
import pe.gob.essalud.apps.dto.marcacioncons.PersonalProjection;

import java.util.List;

@BasicAuthForMarcacionConsService
@FeignClient(name = "marcacionconsserviceclient", url = "${feign-clients.marcacion-cons-service.url}")
public interface MarcacionConsServiceClient {

    @GetMapping("marcaciones/findAllMarcas")
    List<PersonalProjection> findAllMarcas(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam String codigo);

}
