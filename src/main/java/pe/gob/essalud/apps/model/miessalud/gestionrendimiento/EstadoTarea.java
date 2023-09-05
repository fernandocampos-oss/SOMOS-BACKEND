package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "estado_tarea")
public class EstadoTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_tarea")
    private Integer idEstadoTarea;

    @Column(name="descripcion", nullable = false)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;

}