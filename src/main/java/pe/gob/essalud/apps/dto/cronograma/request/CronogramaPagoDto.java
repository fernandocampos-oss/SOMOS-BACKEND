package pe.gob.essalud.apps.dto.cronograma.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CronogramaPagoDto {

    private int idCronogramaPago;
    private int idTipoContrato;
    private String descripcionPeriodo;
    private LocalDate fecha;
    private int[] tipoPagoAsociados;

}
