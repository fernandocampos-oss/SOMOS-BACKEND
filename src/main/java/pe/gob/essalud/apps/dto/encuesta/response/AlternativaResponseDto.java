package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

@Data
public class AlternativaResponseDto {

    private int idAlternativa;
    private String descripcion;
    private int valor;

}