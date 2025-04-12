package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.dto.recaptcha.RecaptchaResponseDto;

@FeignClient(name = "recaptchaenterpriseserviceclient", url = "${feign-clients.recaptcha-enterprise-service.url}")
public interface RecaptchaEnterpriseServiceClient {

    @PostMapping("verify")
    RecaptchaResponseDto verify(@RequestParam String token, @RequestParam String action);

}
