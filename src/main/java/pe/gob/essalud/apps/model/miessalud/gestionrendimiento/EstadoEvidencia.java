package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "estado_evidencia")
public class EstadoEvidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_evidencia")
    private Integer idEstadoEvidencia;

    @Column(name="codigo")
    private String codigo;

    @Column(name="descripcion", nullable = false)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;

}