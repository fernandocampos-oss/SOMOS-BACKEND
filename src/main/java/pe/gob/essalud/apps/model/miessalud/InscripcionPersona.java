package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "inscripcion_persona")
public class InscripcionPersona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ins_persona")
    private Long idInsPersona;
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Column(name = "id_inscripcion")
    private Integer idInscripcion;
    @Column(name = "estado_activo")
    private boolean estadoActivo;
    @Column(name = "fecha_inscripcion", updatable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    private void prePersist() {
        this.fechaInscripcion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
