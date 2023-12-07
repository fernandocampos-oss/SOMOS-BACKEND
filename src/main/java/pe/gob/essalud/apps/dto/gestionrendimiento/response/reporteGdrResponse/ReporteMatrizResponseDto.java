package pe.gob.essalud.apps.dto.gestionrendimiento.response.reporteGdrResponse;

import lombok.Data;

@Data
public class ReporteMatrizResponseDto {
    private String numeroDocumento;
    private String nombreCompleto;
    private String genero;
    private String fechaNacimiento;
    private String regimenLaboral;
    private String correo;
    private String organo;  //unidad
    private String unidad;  //vacio
    private String puesto;
    private String segmento;
    private String rol;
    private String indicador;
}
