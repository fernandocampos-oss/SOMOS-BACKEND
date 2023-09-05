package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name="tarea")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private int idTarea;

    @Column(name="nombre_tarea", nullable = true, length = 170)
    private String nombreTarea;

    @Column(name = "plazo")
    private String plazo;

    @Column(name="porcentaje_avance")
    private Integer porcentajeAvance;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_requerimiento_usuario", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_requerimientousuario"))
    private RequerimientoUsuario requerimientoUsuario;

    @Column(name = "estado")
    private boolean estado;

//    @ManyToOne
//    @JoinColumn(name="id_estado_tarea", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_estadotarea"))
//    private EstadoTarea estadoTarea;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

}

