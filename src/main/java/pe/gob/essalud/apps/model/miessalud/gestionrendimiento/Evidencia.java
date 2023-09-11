package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="evidencia")
public class Evidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia")
    private Long idEvidencia;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name="porcentaje_avance")
    private int porcentajeAvance;

    @Column(name = "estado")
    private boolean estado;

    @ManyToOne
    @JoinColumn(name="id_tarea", nullable = false, foreignKey = @ForeignKey(name="fk_evidencia_tarea"))
    private Tarea tarea;

    @Column(name = "ruta_imagen")
    private String rutaImagen;

    @Column(name = "nombre_imagen")
    private String nombreImagen;

    @Column(name = "size_imagen")
    private int sizeImagen;

    @Column(name = "tipo_imagen")
    private String tipoImagen;

    @Column(name = "extension")
    private String extension;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

}
