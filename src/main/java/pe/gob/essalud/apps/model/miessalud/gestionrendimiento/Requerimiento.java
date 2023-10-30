package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@Table(name="requerimiento")
public class Requerimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requerimiento")
    private Integer idRequerimiento;

    @Column(name="nombre")
    private String nombre;

    @Column(name="descripcion")
    private String descripcion;

//    @Column(name="identificador")
//    private String identificador;

    @Column(name="valor_meta")
    private int valorMeta;

    @Column(name = "estado")
    private boolean estado;

    @ManyToOne
    @JoinColumn(name="id_tipo_ingreso", nullable = false, foreignKey = @ForeignKey(name="fk_requerimiento_tipoingreso"))
    private TipoIngreso tipoIngreso;

    @Column(name = "es_jefe")
    private boolean esJefe;

    @ManyToOne
    @JoinColumn(name="id_tipo_valor_meta", nullable = false, foreignKey = @ForeignKey(name="fk_req_valormeta"))
    private TipoValorMeta tipoValorMeta;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;

    //	@JsonFormat(pattern="MM/dd/yyyy")
    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;

    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

}
