package pe.gob.essalud.apps.dto.captcha;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class CaptchaRequestDto {
    //    @NotEmpty(message = "La llave de captcha el obligatoria")
    private String llave;
}
