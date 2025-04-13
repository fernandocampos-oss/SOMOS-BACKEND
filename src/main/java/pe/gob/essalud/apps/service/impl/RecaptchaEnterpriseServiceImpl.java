package pe.gob.essalud.apps.service.impl;

import pe.gob.essalud.apps.client.RecaptchaEnterpriseServiceClient;
import pe.gob.essalud.apps.dto.captcha.CaptchaResponseDto;
import pe.gob.essalud.apps.dto.recaptcha.RecaptchaResponseDto;
import pe.gob.essalud.apps.service.RecaptchaEnterpriseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecaptchaEnterpriseServiceImpl implements RecaptchaEnterpriseService {

    private final RecaptchaEnterpriseServiceClient recaptchaEnterpriseServiceClient;

    public CaptchaResponseDto verifyToken(String token, String action) {
        CaptchaResponseDto dto = new CaptchaResponseDto();
        try {
            RecaptchaResponseDto recaptchaResponseDto = recaptchaEnterpriseServiceClient.verify(token, action);
            log.info("Recaptcha enterprise response: {}", recaptchaResponseDto);
            if (recaptchaResponseDto != null) {
                dto.setRespuestaCaptcha(recaptchaResponseDto.isSuccess());
            }
        } catch (Exception e) {
            log.error("Error verifyToken recaptcha enterprise", e);
        }
        return dto;
    }

}
