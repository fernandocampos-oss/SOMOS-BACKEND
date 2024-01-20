package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;

@Data
public class EmailNotificacionRequestDto {
    private String email;
    private String url;
}
