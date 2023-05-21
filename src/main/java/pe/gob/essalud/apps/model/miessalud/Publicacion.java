package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "publicacion")
@Where(clause = "es_activo=true")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacion")
    private Long idPublicacion;
    @Column(name = "titulo", nullable = false)
    private String titulo;
    private String descripcion;
    @Column(name = "ruta_imagen")
    private String rutaImagen;
    @Column(name = "url_redireccion")
    private String urlRedireccion;
    @Column(name = "id_sede")
    private Integer idSede;
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

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

    @PreUpdate
    private void preUpdate() {
        this.fechaModificacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
