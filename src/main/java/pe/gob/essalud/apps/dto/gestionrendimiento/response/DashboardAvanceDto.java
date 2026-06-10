package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;
import java.util.List;

@Data
public class DashboardAvanceDto {
    // Datos de persona/unidad/órgano
    private String codigo;
    private String nombre;
    private String tipo; // PERSONA, UNIDAD, ORGANO
    private String codUnidad; // usado internamente para agrupar
    
    // Métricas de avance
    private Integer totalTrabajadores;
    private Integer totalIndicadores;
    private Integer indicadoresCompletados;
    
    // Evidencias por fase
    private Integer evidenciasIniciales;
    private Integer evidenciasSeguimiento;
    private Integer evidenciasFinales;
    private Integer totalEvidencias;
    private Integer evidenciasSubidas;
    
    // Fases GDR (1 = completada, 0 = pendiente)
    private Integer fasePlaneacion;      // Formato registrado
    private Integer faseSeguimiento;     // Todas las evidencias con comentario
    private Integer faseEvaluacion;      // Todos los indicadores con valor alcanzado > 0
    
    // Resultados finales (solo cuando aplica)
    private Integer conCalificacion;
    private Integer sinCalificacion;
    private Integer distinguidos;
    
    // Porcentaje de avance general
    private Double porcentajeAvance;
    
    // Para desglose (solo nivel ORGANO/UNIDAD)
    private List<DashboardAvanceDto> desglose;
}
