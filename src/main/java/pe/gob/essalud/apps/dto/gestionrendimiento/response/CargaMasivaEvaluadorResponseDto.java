package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargaMasivaEvaluadorResponseDto {
    private boolean exito;
    private int totalProcesados;
    private int totalExitosos;
    private int totalErrores;
    private List<EvaluadorPreviewDto> evaluadoresValidos;
    private List<ErrorCargaDto> errores;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluadorPreviewDto {
        private int fila;
        private String dni;
        private String nombreCompleto;
        private String cargo;
        private String unidad;
        private String regimen;
        private boolean yaEsEvaluador;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorCargaDto {
        private int fila;
        private String dni;
        private String mensaje;
    }
}
