package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "usuario_modulo")
public class UsuarioModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_modulo")
    private Long idUsuarioModulo;
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Column(name = "modulo")
    private String modulo;

}
