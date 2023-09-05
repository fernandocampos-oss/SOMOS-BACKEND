package pe.gob.essalud.apps.dto.usuariored.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioDataResponse {

    private long idUsuario;
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
