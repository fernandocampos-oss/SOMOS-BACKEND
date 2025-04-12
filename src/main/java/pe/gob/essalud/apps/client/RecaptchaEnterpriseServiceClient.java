package pe.gob.essalud.apps.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.gob.essalud.apps.config.FormUrlEncodedFeignConfig;
import pe.gob.essalud.apps.dto.recaptcha.RecaptchaResponseDto;

@FeignClient(name = "recaptchaenterpriseserviceclient", url = "${feign-clients.recaptcha-enterprise-service.url}", configuration = FormUrlEncodedFeignConfig.class)
public interface RecaptchaEnterpriseServiceClient {

    @PostMapping(value = "verify", consumes = "application/x-www-form-urlencoded")
    RecaptchaResponseDto verify(@RequestBody MultiValueMap<String, String> formData);

}
