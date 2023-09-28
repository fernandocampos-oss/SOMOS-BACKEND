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
@Table(name = "segmento")
public class Segmento {

    @Id
    @Column(name = "id_segmento")
    private Integer idSegmento;
    @Column(name = "descripcion")
    private String descripcion;

}