package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExcelTrabajadorDto {
    private String evaluadorNombreCompleto;
    private String evaluadorPuesto;
    private String evaluadorCodUnidad;
    private String evaluadorSegmento;
    private String evaluadorNumeroDocumento;
    private String evaluadoNumeroDocumento;
    private String evaluadoNombreCompleto;
    private String evaluadoPuesto;
    private String evaluadoCodUnidad;
    private String evaluadoSegmento;

    private List<PendienteDto> listPrioridad;
}
