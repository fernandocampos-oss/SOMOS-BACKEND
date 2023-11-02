package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="actividad")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer idActividad;

    @Column(name="codigo")
    private String codigo;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name = "estado")
    private boolean estado;

}