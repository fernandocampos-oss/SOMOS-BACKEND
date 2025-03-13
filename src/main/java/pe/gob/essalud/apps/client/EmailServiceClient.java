package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.gob.essalud.apps.dto.emailservice.ActivarCuentaRequestDto;
import pe.gob.essalud.apps.dto.emailservice.RecuperarClaveWebRequestDto;

@FeignClient(name = "emailserviceclient", url = "${feign-clients.email-service.url}")
public interface EmailServiceClient {

    @PostMapping("somos-essalud/activarCuenta")
    boolean activarCuenta(@RequestBody ActivarCuentaRequestDto input);

    @PostMapping("somos-essalud/recuperarClave")
    boolean recuperarClave(@RequestBody RecuperarClaveWebRequestDto input);
}
