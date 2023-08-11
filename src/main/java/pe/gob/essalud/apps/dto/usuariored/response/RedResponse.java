package pe.gob.essalud.apps.dto.usuariored.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedResponse {

    private String codRed;
    private String descripcion;
    private LocalDateTime fechaAsignacion;
    private boolean habilitado;

}
