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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_requerimiento")
    private Integer idEstadoRequerimiento;

    @Column(name="codigo")
    private Integer codigo;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name = "estado")
    private boolean estado;

}

