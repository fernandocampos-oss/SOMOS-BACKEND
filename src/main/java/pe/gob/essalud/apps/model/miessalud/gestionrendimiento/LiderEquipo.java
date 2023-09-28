package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Getter;
import lombok.Setter;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="lider_equipo")
public class LiderEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lider_equipo")
    private Integer idLiderEquipo;

    @ManyToOne
    @JoinColumn(name="id_lider", nullable = false, foreignKey = @ForeignKey(name="fk_liderequipo_usuario"))
    private Usuario lider;

    @ManyToOne
    @JoinColumn(name="id_integrante", nullable = false, foreignKey = @ForeignKey(name="fk_integranteequipo_usuario"))
    private Usuario integrante;

    @Column(name = "es_activo")
    private boolean esActivo;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;
}
