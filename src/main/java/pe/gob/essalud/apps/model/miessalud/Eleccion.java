package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "eleccion")
@Where(clause = "es_activo = true")
public class Eleccion {

    @Id
    @Column(name = "id_eleccion")
    private Integer idEleccion;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    @ManyToMany()
    @JoinTable(
            name = "eleccion_candidato",
            joinColumns = @JoinColumn(name = "id_eleccion"),
            inverseJoinColumns = @JoinColumn(name = "id_candidato")
    )
    private List<Candidato> candidatos;

}