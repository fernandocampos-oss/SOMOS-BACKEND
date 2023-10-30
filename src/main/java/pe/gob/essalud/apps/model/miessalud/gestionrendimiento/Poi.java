package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="poi")
public class Poi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_poi")
    private Integer idPoi;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name = "estado")
    private boolean estado;

}