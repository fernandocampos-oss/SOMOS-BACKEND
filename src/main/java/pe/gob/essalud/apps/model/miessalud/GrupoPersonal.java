package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "grupo_personal")
public class GrupoPersonal {

    @Id
    @Column(name = "id_grupo_personal")
    private Integer idGrupoPersonal;
    private String descripcion;

}