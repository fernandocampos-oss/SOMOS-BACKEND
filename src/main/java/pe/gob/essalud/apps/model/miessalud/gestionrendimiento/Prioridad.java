package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
@Entity
@Table(name="prioridad")
public class Prioridad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridad")
    private Integer idPrioridad;

    @Column(name="descripcion", length = 350)
    private String descripcion;

    @Column(name = "anio")
    private int anio;

    @ManyToOne
    @JoinColumn(name="id_actividad", nullable = false, foreignKey = @ForeignKey(name="fk_prioridad_actividad"))
    private Actividad actividad;

    @OneToMany(mappedBy = "prioridad", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Indicador> listIndicador;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "estado")
    private Boolean estado = true;

    @PrePersist
    private void prePersist() {
        this.fechaAsignacion = LocalDateTime.now(ZoneId.of("America/Lima"));
        if (this.estado == null) {
            this.estado = true;
        }
    }

}
