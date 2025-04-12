package pe.gob.essalud.apps.dto.recaptcha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RecaptchaResponseDto {

    private boolean success;
    private double score;
    private double threshold;
    private String action;
    private List<String> reasons;

}
