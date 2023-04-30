package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "encuesta")
@Where(clause = "es_activo = true")
public class Encuesta {

    @Id
    @Column(name = "id_encuesta")
    private Integer idEncuesta;
    private String descripcion;
    @ManyToMany()
    @JoinTable(
            name = "encuesta_pregunta",
            joinColumns = @JoinColumn(name = "id_encuesta"),
            inverseJoinColumns = @JoinColumn(name = "id_pregunta")
    )
    private List<Pregunta> preguntas;

}