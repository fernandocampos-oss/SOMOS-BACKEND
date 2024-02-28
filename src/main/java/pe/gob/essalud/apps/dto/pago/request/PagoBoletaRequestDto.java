package pe.gob.essalud.apps.dto.pago.request;

import lombok.Data;

@Data
public class PagoBoletaRequestDto {

    private String detalle;
    private String tipoBoleta;

}
