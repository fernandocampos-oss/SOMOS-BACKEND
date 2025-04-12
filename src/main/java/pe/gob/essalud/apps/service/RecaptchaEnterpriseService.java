package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.captcha.CaptchaResponseDto;

public interface RecaptchaEnterpriseService {

    CaptchaResponseDto verifyToken(String token, String action);

}
