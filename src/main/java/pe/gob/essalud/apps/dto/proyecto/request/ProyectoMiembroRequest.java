package pe.gob.essalud.apps.dto.proyecto.request;

import lombok.Data;

@Data
public class ProyectoMiembroRequest {

    private int idProyectoMiembro;
    private String nombre;
    private String dni;
    private String cargo;
    private int idUsuario;

}
