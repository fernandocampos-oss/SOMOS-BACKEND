package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MainTareaDto {
    private String nombre;
    private LocalDateTime plazo;
}
