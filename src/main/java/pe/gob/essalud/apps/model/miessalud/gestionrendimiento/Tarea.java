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

    @Column(name="evidencia_descripcion")
    private String evidenciaDescripcion;

    @Column(name = "evidencia_extension_file")
    private String evidenciaExtensionFile;

    @Column(name = "evidencia_ruta_file")
    private String evidenciaRutaFile;

    @Column(name = "evidencia_fecha_registro")
    private LocalDateTime evidenciaFechaRegistro;

    @Column(name="evaluacion_comentario")
    private String evaluacionComentario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_indicador", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_indicador"))
    private Indicador indicador;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_estado_tarea", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_estadotarea"))
    private EstadoTarea estadoTarea;

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

