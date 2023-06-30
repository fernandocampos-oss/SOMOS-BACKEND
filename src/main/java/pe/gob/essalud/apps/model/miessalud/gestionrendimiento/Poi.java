package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

import javax.persistence.JoinColumn;
import javax.persistence.ForeignKey;

@Data
@Entity
@Table(name="poi")
public class Poi {

    @Id
    @Column(name = "id_poi")
    private Integer idPoi;

    @Column(name="descripcion", nullable = false, length = 250)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name="id_indicador", nullable = false, foreignKey = @ForeignKey(name="fk_poi_indicador"))
    private Indicador indicador;

    @Column(name = "estado", nullable = true)
    private boolean estado;

}