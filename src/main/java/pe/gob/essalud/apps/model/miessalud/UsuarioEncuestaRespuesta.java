package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "usuario_encuesta_respuesta")
public class UsuarioEncuestaRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long idRespuesta;
    @Column(name = "id_usuario_encuesta")
    private Long idUsuarioEncuesta;
    @Column(name = "id_pregunta")
    private Integer idPregunta;
    @Column(name = "id_alternativa")
    private Integer idAlternativa;

}
