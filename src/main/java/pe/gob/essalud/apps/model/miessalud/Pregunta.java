package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "pregunta")
public class Pregunta {

    @Id
    @Column(name = "id_pregunta")
    private Integer idPregunta;
    private String descripcion;

}