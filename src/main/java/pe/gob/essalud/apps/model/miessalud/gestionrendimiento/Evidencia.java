package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "evidencia")
public class Evidencia {

    @Id
    @Column(name = "id_evidencia")
    private Integer idEvidencia;

    @Column(name="descripcion", nullable = false)
    private String descripcion;

    @Column(name="avance", nullable = false)
    private Integer avance;

    @Column(name = "estado", nullable = true)
    private boolean estado;

    @ManyToOne
    @JoinColumn(name="id_tarea", nullable = true, foreignKey = @ForeignKey(name="fk_evidencia_tarea"))
    private Tarea tarea;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "id_usuario_modificacion")
    private Integer idUsuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

}

