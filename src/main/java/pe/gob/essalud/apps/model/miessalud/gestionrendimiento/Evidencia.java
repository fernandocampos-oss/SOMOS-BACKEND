package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name="evidencia")
public class Evidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia")
    private Integer idEvidencia;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name = "plazo")
    private LocalDateTime plazo;

    @Column(name="comentario")
    private String comentario;

    @Column(name="sustento_descripcion")
    private String sustentoDescripcion;

    @Column(name = "sustento_extension_file")
    private String sustentoExtensionFile;

    @Column(name = "sustento_ruta_file")
    private String sustentoRutaFile;

    @Column(name = "sustento_fecha_registro")
    private LocalDateTime sustentoFechaRegistro;

    @Column(name="sustento_comentario")
    private String sustentoComentario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_indicador", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_indicador"))
    private Indicador indicador;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_estado_evidencia", nullable = false, foreignKey = @ForeignKey(name="fk_evidencia_estadoevidencia"))
    private EstadoEvidencia estadoEvidencia;

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

    @PrePersist@PreUpdate
    private void preUpdate() {
        this.fechaModificacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }


}

