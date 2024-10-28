package pe.gob.essalud.apps.model.miessalud.encuestapublicacion;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "form_pregunta")
public class FormPregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_form_pregunta")
    private Integer idFormPregunta;

    @Column(name = "denominacion")
    private String denominacion;

    @Column(name = "respuesta")
    private String respuesta;

    @ManyToOne
    @JoinColumn(name="id_form_encuesta", nullable = true, foreignKey = @ForeignKey(name="fk_pregunta_formencuesta"))
    private FormEncuesta formEncuesta;
}
