package pe.gob.essalud.apps.dto.usuariored.response;

import lombok.Data;

@Data
public class DatoRedResponse {

    private String codRed;
    private String descripcion;
    private int cantidad;
    private int personalAsistencial;
    private int personalAdministrativo;
}
