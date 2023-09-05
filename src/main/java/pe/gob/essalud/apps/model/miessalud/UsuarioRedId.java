package pe.gob.essalud.apps.model.miessalud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UsuarioRedId implements Serializable {

    private Long idUsuario;
    private String codRed;

}
