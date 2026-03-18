package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExcelIndicadorDto {
    private int idIndicador;
    private String nombreIndicador;
    private String codTipoValorMeta;
    private int valorMeta;
    private int peso;
    private String sentido;           // "Ascendente" o "Descendente"
    private BigDecimal valorAlcanzado; // Valor alcanzado (viene de valor_alcanzado_prioridad)
    private BigDecimal puntajePorMeta; // Puntaje calculado
    private List<ExcelEvidenciaDto> listEvidencia;
}
