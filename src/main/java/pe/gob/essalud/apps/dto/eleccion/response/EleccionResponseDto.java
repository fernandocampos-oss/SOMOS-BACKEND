package pe.gob.essalud.apps.dto.eleccion.response;

import lombok.Data;

import java.util.List;

@Data
public class EleccionResponseDto {

    private int idEleccion;
    private String descripcion;
    private int idSegmento;
    private List<CandidatoResponseDto> candidatos;

}
