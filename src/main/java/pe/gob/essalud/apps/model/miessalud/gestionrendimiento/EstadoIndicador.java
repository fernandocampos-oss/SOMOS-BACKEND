package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "estado_indicador")
public class EstadoIndicador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_indicador")
    private Integer idEstadoIndicador;

    @Column(name="codigo")
    private String codigo;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name = "estado")
    private boolean estado;

}

