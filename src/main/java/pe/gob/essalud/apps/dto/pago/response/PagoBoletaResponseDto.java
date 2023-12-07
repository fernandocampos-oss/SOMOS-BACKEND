package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoBoletaResponseDto {

    private int idBoleta;
    private LocalDateTime fechaEmision;
    private String regimen;
    private String tipoBoleta;

}
