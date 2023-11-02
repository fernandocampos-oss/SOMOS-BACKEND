package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
    private Integer idTarea;

    @Column(name="nombre")
    private String nombre;

    @Column(name = "plazo")
    private LocalDateTime plazo;

    @Column(name="motivo_rechazo")
    private String motivoRechazo;

    @Column(name="peso")
    private int peso;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_indicador_usuario", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_indicadorousuario"))
    private IndicadorUsuario indicadorUsuario;

    @Column(name = "estado")
    private boolean estado;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;
    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;
    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}

