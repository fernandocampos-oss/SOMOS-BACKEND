package pe.gob.essalud.apps.dto.eleccion.request;

import lombok.Data;

@Data
public class VotoRequestDto {

    private int idEleccion;
    private int idCandidato;
    private int idSegmento;

}
