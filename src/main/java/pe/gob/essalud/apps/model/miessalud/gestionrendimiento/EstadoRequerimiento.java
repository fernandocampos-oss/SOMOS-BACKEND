package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "estado_requerimiento")
public class EstadoRequerimiento {

    @Id
    @Column(name = "id_estado_requerimiento")
    private Integer idEstadoRequerimiento;

    @Column(name="codigo", nullable = false, length = 5)
    private Integer codigo;

    @Column(name="descripcion", nullable = false, length = 50)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;

}

