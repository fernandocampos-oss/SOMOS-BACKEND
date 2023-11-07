package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UsuariosInscritosResponseDto {

    private int idUsuario;
    private String numeroDocumento;
    private String nombreCompleto;
    private String regimen;
    private String numeroCelular;
    private String correo;
    private boolean estadoActivo;
    private LocalDateTime fechaInscripcion;
    private String descripcion;
    public Integer idLider;
    private String rutaImagen;

}
