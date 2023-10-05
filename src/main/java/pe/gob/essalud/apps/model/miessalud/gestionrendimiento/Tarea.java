package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name="nombre_tarea", nullable = true, length = 170)
    private String nombreTarea;

    @Column(name = "plazo")
    private LocalDateTime plazo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_requerimiento_usuario", nullable = false, foreignKey = @ForeignKey(name="fk_tarea_requerimientousuario"))
    private RequerimientoUsuario requerimientoUsuario;

    @Column(name = "estado")
    private boolean estado;

    @Column(name="porcentaje_inicial")
    private int porcentajeInicial;

    @Column(name = "tiene_imagen")
    private int tieneImagen;

    @Column(name = "tiene_pdf")
    private int tienePdf;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

}

