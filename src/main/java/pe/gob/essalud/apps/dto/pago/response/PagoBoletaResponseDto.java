package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoBoletaResponseDto {

    private String codigoPlanilla;
    private String regimen;
    private String red;
    private String unidad;
    private String tipoBoleta;
    private String pdfBase64;

}
