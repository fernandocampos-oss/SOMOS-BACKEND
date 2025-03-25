package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PendienteIndicadorDto {
    private int idIndicador;
    private String nombreIndicador;
    private String codTipoValorMeta;
    private int idTipoValorMeta;
    private int valorMeta;
    private int sentido;
    private int peso;
    private boolean asisteReunion;
    private LocalDateTime fechaReunion;
    private List<PendienteEvidenciaDto> listEvidencia;
}
