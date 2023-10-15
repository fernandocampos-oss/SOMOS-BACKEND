package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "inscripcion_voto")
public class InscripcionVoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ins_voto")
    private Integer idInsVoto;
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Column(name = "id_inscripcion")
    private Integer idInscripcion;
    @Column(name = "id_candidato")
    private Integer idCandidato;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }
}