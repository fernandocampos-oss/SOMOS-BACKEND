package pe.gob.essalud.apps.model.miessalud;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "red_personal")
public class RedPersonal {

    @Id
    @Column(name = "cod_red")
    private String codRed;
    private String descripcion;

}
