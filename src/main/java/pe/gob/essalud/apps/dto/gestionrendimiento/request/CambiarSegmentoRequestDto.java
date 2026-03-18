package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarSegmentoRequestDto {
    private Integer idVotante;
    private Integer nuevoSegmento;
}
