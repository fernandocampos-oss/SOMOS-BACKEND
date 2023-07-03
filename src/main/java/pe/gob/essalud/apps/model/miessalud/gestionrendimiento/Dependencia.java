package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "dependencia")
public class Dependencia {

    @Id
    @Column(name = "id_dependencia")
    private Integer idDependencia;

    @Column(name="abreviatura", nullable = false)
    private String abreviatura;

    @Column(name="descripcion", nullable = true)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;

}
