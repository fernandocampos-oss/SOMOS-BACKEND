package pe.gob.essalud.apps.dto.personal.request;

import lombok.Data;

@Data
public class PersonalLogeoRequestDto {
    private String nombres;
    private String dni;
    private String correo;
}
