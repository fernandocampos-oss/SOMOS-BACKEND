package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

@Data
public class PagoBoletaResponseDto {

    private int anio;
    private String mes;
    private String regimen;
    private String tipoBoleta;
    private String periodo;
    private String descripcion;

}
