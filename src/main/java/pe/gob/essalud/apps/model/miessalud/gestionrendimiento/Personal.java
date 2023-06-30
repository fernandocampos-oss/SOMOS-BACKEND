package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="Personal")
public class Personal {

    @Id
    @Column(name = "id_personal")
    private Integer idPersonal;

    @Column(name="numero_dni", nullable = false)
    private String numeroDni;

    @Column(name="nombres", nullable = false, length = 50)
    private String nombres;

    @Column(name="apellido_paterno", nullable = false, length = 150)
    private String apellidoPaterno;

    @Column(name="apellido_materno", nullable = false, length = 150)
    private String apellidoMaterno;

    @Column(name = "estado", nullable = true)
    private Character estado;

    //	@JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_cargo", nullable = true, foreignKey = @ForeignKey(name="fk_personal_cargo"))
    private Cargo cargo;

    @ManyToOne
    @JoinColumn(name="id_dependencia", nullable = true, foreignKey = @ForeignKey(name="fk_personal_dependencia"))
    private Dependencia dependencia;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "id_usuario_modificacion")
    private Integer idUsuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idPersonal == null) ? 0 : idPersonal.hashCode());
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
        Personal other = (Personal) obj;
        if (idPersonal == null) {
            if (other.idPersonal != null)
                return false;
        } else if (!idPersonal.equals(other.idPersonal))
            return false;
        return true;
    }

}
