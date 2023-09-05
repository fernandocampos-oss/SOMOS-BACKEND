package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "proyecto")
@Where(clause = "es_activo=true")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer idProyecto;
    @Column(name = "enviado")
    private boolean enviado;
    @Column(name = "es_activo")
    private boolean esActivo;
    @Column(name = "usuario_creacion", nullable = false)
    private Integer usuarioCreacion;
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    @OneToOne(mappedBy = "proyecto")
    private ProyectoGrupo proyectoGrupo;
    @OneToMany(mappedBy = "proyecto")
    private List<ProyectoMiembro> proyectoMiembros;
    @OneToOne(mappedBy = "proyecto")
    private ProyectoDescripcion proyectoDescripcion;
    @OneToOne(mappedBy = "proyecto")
    private ProyectoImplementacion proyectoImplementacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

    @PreUpdate
    private void preUpdate() {
        this.fechaModificacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
