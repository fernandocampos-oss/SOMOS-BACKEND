package pe.gob.essalud.apps.dto.recaptcha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RecaptchaResponseDto {

    private boolean success;
    private double score;
    private double threshold;
    private String action;

}
