package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class MainDto {
    private int idVotante;
    private String trabajadorNombre;
    private String trabajadorApellido;
    private String email;
    private int pesoTotal;
    private String evaluadoSegmento;
    private List<MainPrioridadDto> listPrioridad;
}
