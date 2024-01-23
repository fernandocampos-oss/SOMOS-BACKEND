package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

@Data
public class BoletaPagoResponseDto {

    private String codigoPlanilla;
    private String fechaConsulta;
    private String regimen;
    private String red;
    private String unidad;
    private String tipoBoleta;
    private String pdfBase64;

}
