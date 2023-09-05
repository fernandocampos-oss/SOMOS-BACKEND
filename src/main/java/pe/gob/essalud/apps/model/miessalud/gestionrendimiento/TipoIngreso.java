package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name="tipo_ingreso")
public class TipoIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_ingreso")
    private Integer idTipoIngreso;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name = "estado")
    private boolean estado;
}
