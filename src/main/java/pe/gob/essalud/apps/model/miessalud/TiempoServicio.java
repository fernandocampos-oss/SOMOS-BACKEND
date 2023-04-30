package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "tiempo_servicio")
public class TiempoServicio {

    @Id
    @Column(name = "id_tiempo_servicio")
    private Integer idTiempoServicio;
    private String descripcion;

}