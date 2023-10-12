package pe.gob.essalud.apps.dto.usuariored.response;

import lombok.Data;

import java.util.List;

@Data
public class DatosRedesAsignadasResponse {

    private List<DatoRedResponse> datosRedes;
    private int cuentaTotalUsuarios;
    private int cuentaAsistenciales;
    private int cuentaAdministrativos;
}
