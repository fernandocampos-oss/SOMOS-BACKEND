package pe.gob.essalud.apps.dto.usuario.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioRegisterUpdateRequestDto {

    private String numeroDocumento;
    private String codigoPlanilla;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String numeroCelular;
    private String correo;
    private int idRol;

}
