package pe.gob.essalud.apps.dto.proyecto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProyectoGrupoRequest {

    private int idProyectoGrupo;
    private String nombre;
    private String sede;
    private String jefe;
    private String categoria;
    private String imagenBase64;
    private List<ProyectoMiembroRequest> miembros;

}
