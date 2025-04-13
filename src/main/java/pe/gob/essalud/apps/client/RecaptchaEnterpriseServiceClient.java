package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.essalud.apps.config.FormUrlEncodedFeignConfig;
import pe.gob.essalud.apps.dto.recaptcha.RecaptchaResponseDto;

@FeignClient(name = "recaptchaenterpriseserviceclient", url = "${feign-clients.recaptcha-enterprise-service.url}", configuration = FormUrlEncodedFeignConfig.class)
public interface RecaptchaEnterpriseServiceClient {

    @PostMapping(value = "verify", consumes = "application/x-www-form-urlencoded")
    RecaptchaResponseDto verify(@RequestParam("token") String token, @RequestParam("action") String action);

}
