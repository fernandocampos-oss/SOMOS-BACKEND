package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;
    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

}
