package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Data
@Entity
@Table(name="tipo_ingreso")
public class TipoIngreso {

    @Id
    @Column(name = "id_tipo_ingreso")
    private Integer idTipoIngreso;

    @Column(name="descripcion", nullable = false, length = 250)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;
}
