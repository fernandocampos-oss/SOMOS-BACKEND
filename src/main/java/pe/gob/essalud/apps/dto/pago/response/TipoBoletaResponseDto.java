package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

@Data
public class TipoBoletaResponseDto {

    private int idTipoBoleta;
    private String tipo;
    private String descripcion;

}
