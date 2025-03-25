package pe.gob.essalud.apps.dto.onomastico.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OnomasticoResponseDto {
    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String fechaNacimiento;
    private String correo;
    private Boolean esActivo;
    private String unidad;
}
