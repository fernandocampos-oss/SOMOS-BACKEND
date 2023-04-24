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
@Table(name = "onomastico")
public class Onomastico {

    @Id
    @Column(name = "id_onomastico")
    private Integer idOnomastico;
    private String nombres;
    private String dia;
    private String mes;
    private String dependencia;

}
