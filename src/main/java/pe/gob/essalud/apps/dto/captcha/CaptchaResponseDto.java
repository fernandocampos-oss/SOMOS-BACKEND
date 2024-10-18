package pe.gob.essalud.apps.dto.captcha;

import lombok.Data;

@Data
public class CaptchaResponseDto {
    private int cantidad;
    private boolean respuestaCaptcha;
}

