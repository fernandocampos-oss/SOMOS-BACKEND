package pe.gob.essalud.apps.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSessionDto {

    private int id;
    private String nombres;
    private int idSede;
    private Integer idZonaControl;
    private int idRol;

}
