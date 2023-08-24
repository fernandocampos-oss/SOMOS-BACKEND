package pe.gob.essalud.apps.dto.proyecto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProyectoMiembroRequest {

    private int idProyectoMiembro;
    private int idUsuario;
    private String numeroDocumento;
    private String codigoPlanilla;
    private String cargo;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String numeroCelular;
    private String correo;
    private String red;

}
