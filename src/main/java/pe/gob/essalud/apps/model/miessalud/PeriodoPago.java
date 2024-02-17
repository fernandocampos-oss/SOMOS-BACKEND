package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "periodo_pago")
public class PeriodoPago {

    @Id
    @Column(name = "id_periodo_pago")
    private Integer idPeriodoPago;
    private String descripcion;

}
