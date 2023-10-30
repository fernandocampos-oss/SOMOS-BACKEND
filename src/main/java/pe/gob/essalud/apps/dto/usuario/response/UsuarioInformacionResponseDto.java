package pe.gob.essalud.apps.dto.usuario.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioInformacionResponseDto {
    private String numeroDocumento;
    private String codigoPlanilla;
    private String nombres;
    private String apellidos;
    private LocalDateTime fechaCreacion;
    private String correo;
    private String numeroCelular;
    private String regimen;
    private String cargo;
    private String codRed;
    private String descripcionRed;
    private String codUnidad;
    private String descripcionUnidad;
    private String sexo;
}
