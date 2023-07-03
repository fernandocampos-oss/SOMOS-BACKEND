package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name="requerimiento_personal")
public class RequerimientoPersonal {

    @Id
    @Column(name = "id_requerimiento_personal")
    private Integer idRequerimientoPersonal;

    @ManyToOne
    @JoinColumn(name="id_personal", nullable = false, foreignKey = @ForeignKey(name="fk_requerimientopersonal_personal"))
    private Personal personal;

    @ManyToOne
    @JoinColumn(name="id_requerimiento", nullable = false, foreignKey = @ForeignKey(name="fk_requerimientopersonal_requerimiento"))
    private Requerimiento requerimiento;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "requerimientoPersonal", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Tarea> listTarea;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idRequerimientoPersonal == null) ? 0 : idRequerimientoPersonal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequerimientoPersonal other = (RequerimientoPersonal) obj;
        if (idRequerimientoPersonal == null) {
            if (other.idRequerimientoPersonal != null)
                return false;
        } else if (!idRequerimientoPersonal.equals(other.idRequerimientoPersonal))
            return false;
        return true;
    }

}
