package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class PendienteIndicadorDto {
    private int idIndicador;
    private String nombreIndicador;
    private String codTipoValorMeta;
    private int idTipoValorMeta;
    private int valorMeta;
    private int peso;
    private List<PendienteEvidenciaDto> listEvidencia;
}
