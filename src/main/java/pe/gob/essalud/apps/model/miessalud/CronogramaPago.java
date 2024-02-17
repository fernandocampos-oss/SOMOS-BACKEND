package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cronograma_pago")
public class CronogramaPago {

    @Id
    @Column(name = "id_cronograma_pago")
    private Integer idCronogramaPago;
    @ManyToOne
    @JoinColumn(name = "id_tipo_contrato")
    private TipoContrato tipoContrato;
    @ManyToOne
    @JoinColumn(name = "id_periodo_pago")
    private PeriodoPago periodoPago;
    private int dia;
    private int mes;
    @Column(name = "tipo_pago_asociado")
    private String tipoPagoAsociado;

}
