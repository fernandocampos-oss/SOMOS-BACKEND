package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

import java.util.List;

@Data
public class ReporteAlternativaResponseDto {

    private Integer idAlternativa;
    private String descripcion;
    private Integer cantidadEleccion;
    private List<Long> usuariosEleccion;
}
