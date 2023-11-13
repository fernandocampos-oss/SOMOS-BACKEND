package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

@Data
public class ReporteUsuarioEncuestaResponseDto {

    private Long idUsuarioEncuesta;
    private Long idUsuario;
    private String numeroDocumento;
    private String codigoPlanilla;
    private String nombreCompleto;
    private String red;
    private String unidadOrganica;
    private String cargo;
    private String regimen;
    private String numeroCelular;
    private String correo;
}
