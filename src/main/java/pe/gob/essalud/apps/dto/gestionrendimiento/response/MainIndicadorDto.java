package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class MainIndicadorDto {
    private int idIndicador;
    private String nombreIndicador;
    private String codTipoValorMeta;
    private int idTipoValorMeta;
    private int valorMeta;
    private int sentido;
    private int peso;
    private List<MainEvidenciaDto> listEvidencia;
    
    /* Agregado de 2 columnas - Inicio */
    
    private String desPrioridad;
    private String flDesPrioridad;
    
    /* Agregado de 2 columnas - Fin */
}
