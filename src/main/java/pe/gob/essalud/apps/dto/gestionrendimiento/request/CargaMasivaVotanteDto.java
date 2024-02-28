package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.Data;

@Data
public class CargaMasivaVotanteDto {
    private int idVotante;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private int idSegmento;
    private int idUsuario;
}
