package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;

@Data
@Entity
@Table(name="tarea")
public class Tarea {

    @Id
    @Column(name = "id_tarea")
    private int idTarea;

    @Column(name="nombre_tarea", nullable = true, length = 170)
    private String nombreTarea;

    @Column(name = "plazo")
    private String plazo;

    @Column(name = "estado", nullable = true)
    private boolean estado;

    @Column(name="estado_avance", nullable = true)
    private String estadoAvance;

    @Column(name="porcentaje_avance", nullable = true, length = 3)
    private Integer porcentajeAvance;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_requerimiento_personal", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_requerimientopersonal"))
    private RequerimientoPersonal requerimientoPersonal;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "id_usuario_modificacion")
    private Integer idUsuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

}

