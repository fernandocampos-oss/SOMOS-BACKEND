package pe.gob.essalud.apps.dto.gestionrendimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExcelTareaDto {
    private int idTarea;
    private String nombre;
    private LocalDateTime plazo;

    private LocalDateTime fechaCreacion;
    private String motivoRechazo;
    private String evidenciaDescripcion;
    private LocalDateTime evidenciaFechaRegistro;
    private String evidenciaExtensionFile;
}
