package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="requerimiento")
public class Requerimiento {

    @Id
    @Column(name = "id_requerimiento")
    private Integer idRequerimiento;

    @Column(name="nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name="descripcion", nullable = true, length = 350)
    private String descripcion;

    @Column(name="identificador", nullable = true, length = 150)
    private String identificador;

    @Column(name="motivo", nullable = true, length = 350)
    private String motivo;

    @Column(name="porcentaje_avance", nullable = true, length = 3)
    private Integer porcentajeAvance;

    @Column(name = "estado", nullable = false)
    private boolean estado;

    @ManyToOne
    @JoinColumn(name="id_area_solicitante", nullable = true, foreignKey = @ForeignKey(name="fk_requerimiento_area_solicitante"))
    private Dependencia areaSolicitante;

    @ManyToOne
    @JoinColumn(name="id_area_receptor", nullable = true, foreignKey = @ForeignKey(name="fk_requerimiento_area_receptor"))
    private Dependencia areaReceptor;

    @ManyToOne
    @JoinColumn(name="id_tipo_ingreso", nullable = false, foreignKey = @ForeignKey(name="fk_requerimiento_tipo_ingreso"))
    private TipoIngreso tipoIngreso;

    @ManyToOne
    @JoinColumn(name="id_estado_requerimiento", nullable = false, foreignKey = @ForeignKey(name="fk_requerimiento_estado_req"))
    private EstadoRequerimiento estadoRequerimiento;

    //	@JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_poi", nullable = true, foreignKey = @ForeignKey(name="fk_requerimiento_poi"))
    private Poi poi;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "id_usuario_modificacion")
    private Integer idUsuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

}
