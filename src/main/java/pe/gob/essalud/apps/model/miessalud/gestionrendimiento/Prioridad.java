package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="prioridad")
public class Prioridad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridad")
    private Integer idPrioridad;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name="peso")
    private int peso;

    @Column(name = "anio_registro")
    private int  anioRegistro;

    @ManyToOne
    @JoinColumn(name="id_actividad", nullable = false, foreignKey = @ForeignKey(name="fk_prioridad_actividad"))
    private Actividad actividad;

    @OneToMany(mappedBy = "prioridad", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Indicador> listIndicador;

}
