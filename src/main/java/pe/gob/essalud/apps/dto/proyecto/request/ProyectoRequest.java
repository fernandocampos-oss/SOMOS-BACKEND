package pe.gob.essalud.apps.dto.proyecto.request;

import lombok.Data;

@Data
public class ProyectoRequest {

    private int idProyecto;
    private boolean enviado;
    private int idUsuario;
    private ProyectoGrupoRequest grupo;
    private ProyectoDescripcionRequest descripcion;
    private ProyectoImplementacionRequest implementacion;

}
