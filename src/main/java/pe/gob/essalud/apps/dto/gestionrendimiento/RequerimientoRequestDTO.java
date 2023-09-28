package pe.gob.essalud.apps.dto.gestionrendimiento;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequerimientoRequestDTO {

    private int idRequerimiento;
    private String nombre;
    private String descripcion;
    private String identificador;
    private int porcentajeAvance;
    private TipoIngreso tipoIngreso;
    private boolean estado;
    private boolean jefe;
    private LocalDateTime fechaCreacion;

}