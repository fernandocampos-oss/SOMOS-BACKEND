package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForEmailService;
import pe.gob.essalud.apps.dto.emailservice.ActivarCuentaRequestDto;
import pe.gob.essalud.apps.dto.emailservice.RecuperarClaveWebRequestDto;
import pe.gob.essalud.apps.dto.emailservice.SaludoOnomasticobRequestDto;

@BasicAuthForEmailService
@FeignClient(name = "emailserviceclient", url = "${feign-clients.email-somos-service.url}")
public interface EmailServiceClient {

    @PostMapping("somos-essalud/activarCuenta")
    boolean activarCuenta(@RequestBody ActivarCuentaRequestDto input);

    @PostMapping("somos-essalud/recuperarClave")
    boolean recuperarClave(@RequestBody RecuperarClaveWebRequestDto input);

    @PostMapping("somos-essalud/saludoOnomastico")
    boolean saludoOnomastico(@RequestBody SaludoOnomasticobRequestDto input);

}
