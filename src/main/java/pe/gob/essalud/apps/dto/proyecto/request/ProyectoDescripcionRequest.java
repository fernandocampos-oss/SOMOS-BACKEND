package pe.gob.essalud.apps.dto.proyecto.request;

import lombok.Data;

@Data
public class ProyectoDescripcionRequest {

    private int idProyectoDescripcion;
    private String fecha;
    private String motivo;
    private String descripcion;
    private String contexto;
    private String innovacion;
    private String indicador;

}
