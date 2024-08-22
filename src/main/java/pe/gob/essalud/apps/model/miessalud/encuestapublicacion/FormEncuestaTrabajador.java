package pe.gob.essalud.apps.model.miessalud.encuestapublicacion;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "form_encuesta_trabajador")
public class FormEncuestaTrabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_form_encuesta_trabajador")
    private Integer idFormEncuestaTrabajador;

    @Column(name = "id_form_encuesta")
    private Integer idFormEncuesta;

    @ManyToOne
    @JoinColumn(name="id_form_pregunta", nullable = true, foreignKey = @ForeignKey(name="fk_respuesta_pregunta"))
    private FormPregunta formPregunta;

    @Column(name = "id_trabajador")
    private Integer idTrabajador;

    @Column(name = "satisfecho")
    private String satisfecho;

    @Column(name = "por_mejorar")
    private String porMejorar;

    @Column(name = "insatisfecho")
    private String insatisfecho;

    @Column(name = "finalizado")
    private boolean finalizado;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
    @PrePersist
    private void prePersist() {
        this.fechaRespuesta = LocalDateTime.now(ZoneId.of("America/Lima"));
    }
}
