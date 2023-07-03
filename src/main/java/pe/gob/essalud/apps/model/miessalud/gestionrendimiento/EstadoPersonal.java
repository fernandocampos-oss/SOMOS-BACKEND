package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "estado_personal")
public class EstadoPersonal {

    @Id
    @Column(name = "id_estado_personal")
    private Integer idEstadoPersonal;

    @Column(name="descripcion", nullable = false)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;
}
