package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "estado_tarea")
public class EstadoTarea {

    @Id
    @Column(name = "id_estado_tarea")
    private Integer idEstadoTarea;

    @Column(name="descripcion", nullable = false)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;

}