package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class MainIndicadorDto {
    private int idIndicador;
    private String nombreIndicador;
    private String codTipoValorMeta;
    private int valorMeta;
    private int peso;
    private List<MainEvidenciaDto> listEvidencia;
}
