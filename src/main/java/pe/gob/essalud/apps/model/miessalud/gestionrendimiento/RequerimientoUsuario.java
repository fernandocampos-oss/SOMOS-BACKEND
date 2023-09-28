package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name="requerimiento_usuario")
public class RequerimientoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requerimiento_usuario")
    private Integer idRequerimientoUsuario;

    @Column(name="motivo")
    private String motivo;

    @Column(name="cod_red")
    private String codRed;

    @Column(name="cod_unidad_solicitante")
    private String codUnidadSolicitante;

    @Column(name="cod_unidad_receptor")
    private String codUnidadReceptor;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false, foreignKey = @ForeignKey(name="fk_requerimientousuario_usuario"))
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="id_requerimiento", nullable = false, foreignKey = @ForeignKey(name="fk_requerimientopersonal_requerimiento"))
    private Requerimiento requerimiento;

    @ManyToOne
    @JoinColumn(name="id_estado_requerimiento", nullable = false, foreignKey = @ForeignKey(name="fk_requerimiento_estadorequerimiento"))
    private EstadoRequerimiento estadoRequerimiento;

    @ManyToOne
    @JoinColumn(name="id_poi", nullable = true, foreignKey = @ForeignKey(name="fk_requerimientousuario_poi"))
    private Poi poi;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "es_jefe")
    private boolean esJefe;

    @OneToMany(mappedBy = "requerimientoUsuario", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Tarea> listTarea;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idRequerimientoUsuario == null) ? 0 : idRequerimientoUsuario.hashCode());
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
        RequerimientoUsuario other = (RequerimientoUsuario) obj;
        if (idRequerimientoUsuario == null) {
            if (other.idRequerimientoUsuario != null)
                return false;
        } else if (!idRequerimientoUsuario.equals(other.idRequerimientoUsuario))
            return false;
        return true;
    }

}
