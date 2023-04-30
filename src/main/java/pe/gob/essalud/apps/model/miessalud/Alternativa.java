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
@Table(name = "alternativa")
public class Alternativa {

    @Id
    @Column(name = "id_alternativa")
    private Integer idAlternativa;
    private String descripcion;
    private Integer valor;

}