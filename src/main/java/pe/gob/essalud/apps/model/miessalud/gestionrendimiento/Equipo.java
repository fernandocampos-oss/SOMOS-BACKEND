package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Getter;
import lombok.Setter;
import pe.gob.essalud.apps.model.miessalud.Votante;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name="equipo")
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Integer idEquipo;

    @ManyToOne
    @JoinColumn(name="id_jefe", nullable = false, foreignKey = @ForeignKey(name="fk_equipojefe_votante"))
    private Votante jefe;

    @ManyToOne
    @JoinColumn(name="id_integrante", nullable = false, foreignKey = @ForeignKey(name="fk_equipointegrante_votante"))
    private Votante integrante;

    @Column(name = "es_activo")
    private boolean esActivo;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;
    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;
    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
