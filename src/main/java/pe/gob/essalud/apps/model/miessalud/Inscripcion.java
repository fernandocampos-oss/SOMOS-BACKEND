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
@Table(name = "inscripcion")
@Where(clause = "es_activo = true")
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscripcion")
    private int idInscripcion;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "es_activo")
    private boolean esActivo;
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "id_responsable")
    private String idResponsable;
    @Column(name = "id_publicacion")
    private Long idPublicacion;
    @Column(name = "votacion")
    private boolean votacion;
    @Column(name = "voto_activo")
    private boolean votoActivo;
    @Column(name = "imagen_activa")
    private boolean imagenActiva;
    @Column(name = "imagen_descripcion")
    private String imagenDescripcion;
    @Column(name = "texto_activo")
    private boolean textoActivo;
    @Column(name = "texto_descripcion")
    private String textoDescripcion;
    @Column(name = "grupo_activo")
    private boolean grupoActivo;
    @Column(name = "grupo_longitud")
    private Integer grupoLongitud;


    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
