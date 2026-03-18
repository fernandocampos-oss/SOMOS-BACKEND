package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.util.List;

@Data
public class ExcelDto {
    private String evaluadorNombreCompleto;
    private String evaluadorPuesto;
    private String evaluadorCodUnidad;
    private String evaluadorSegmento;
    private String evaluadorNumeroDocumento;

    private String evaluadoNombreCompleto;
    private String evaluadoPuesto;
    private String evaluadoCodUnidad;
    private String evaluadoSegmento;
    private String evaluadoNumeroDocumento;

    // Datos de reunión establecimiento de metas
    private String reunionAsistio;     // "Sí", "No" o "-"
    private String reunionFecha;       // "dd/MM/yyyy" o "-"

    private List<ExcelPrioridadDto> listPrioridad;
}
