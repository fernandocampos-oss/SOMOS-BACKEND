package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoBoletaResponseDto {

    private BoletaSAP boleta;
    private PdfSAP pdf;

}
