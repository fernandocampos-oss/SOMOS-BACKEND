package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "usuario_encuesta")
public class UsuarioEncuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_encuesta")
    private Long idUsuarioEncuesta;
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Column(name = "id_encuesta")
    private Integer idEncuesta;
    @Column(name = "id_sede")
    private Integer idSede;
    @Column(name = "id_grupo_personal")
    private Integer idGrupoPersonal;
    @Column(name = "id_area_personal")
    private Integer idAreaPersonal;
    @Column(name = "id_tiempo_servicio")
    private Integer idTiempoServicio;
    @CreationTimestamp
    private LocalDateTime fecha;

}
