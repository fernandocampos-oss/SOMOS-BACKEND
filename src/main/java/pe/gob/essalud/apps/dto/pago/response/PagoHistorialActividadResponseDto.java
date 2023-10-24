package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoHistorialActividadResponseDto {

    private String accion;
    private String detalle;
    private String tipoBoleta;
    private LocalDateTime fechaCreacion;

}
