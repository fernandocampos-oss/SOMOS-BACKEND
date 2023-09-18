package pe.gob.essalud.apps.dto.eleccion.response;

import lombok.Data;

@Data
public class CandidatoResponseDto {

    private int idCandidato;
    private long idUsuario;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private int idSegmento;

}
