package pe.gob.essalud.apps.dto.usuario.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioResponseDto {

    private long idUsuario;
    private String numeroDocumento;
    private String codigoPlanilla;
    private String cargo;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String numeroCelular;
    private String correo;
    private Integer idRol;
    private String idEstado;
    private String estado;
    private String red;
    private String unidad;
    @JsonIgnore
    private String rutaImagenPerfil;
    @JsonIgnore
    private String rutaImagenFirma;
    private String imagenPerfilBase64;
    private String imagenFirmaBase64;

}
