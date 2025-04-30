package pe.gob.essalud.apps.service.impl;

import org.springframework.beans.factory.annotation.Value;
import pe.gob.essalud.apps.client.RecaptchaEnterpriseServiceClient;
import pe.gob.essalud.apps.dto.recaptcha.RecaptchaResponseDto;
import pe.gob.essalud.apps.service.RecaptchaEnterpriseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecaptchaEnterpriseServiceImpl implements RecaptchaEnterpriseService {

    @Value("${google.recaptcha.validation.enabled}")
    private boolean captchaValidationEnabled;

    private final RecaptchaEnterpriseServiceClient recaptchaEnterpriseServiceClient;

    public boolean verifyToken(String token, String action) {
        try {
            if (!captchaValidationEnabled) {
                return true;
            }

            RecaptchaResponseDto recaptchaResponseDto = recaptchaEnterpriseServiceClient.verify(token, action);
            log.info("Recaptcha enterprise response: {}", recaptchaResponseDto);
            if (recaptchaResponseDto != null) {
                return recaptchaResponseDto.isSuccess();
            }
        } catch (Exception e) {
            log.error("Error verifyToken recaptcha enterprise", e);
        }
        return false;
    }

}

