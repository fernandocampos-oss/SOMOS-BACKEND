package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluadorListResponseDto {
    private Integer idVotante;
    private String numeroDocumento;
    private String nombreCompleto;
    private String cargo;
    private String unidad;
    private String regimen;
    private Integer idSegmento;
    private Boolean tieneTrabajadores;
}
