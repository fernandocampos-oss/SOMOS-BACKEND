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
@Table(name = "tipo_contrato")
public class TipoContrato {

    @Id
    @Column(name = "id_tipo_contrato")
    private Integer idTipoContrato;
    private String descripcion;
    private String codigo;

}
