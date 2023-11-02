package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@Table(name="indicador")
public class Indicador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_indicador")
    private Integer idIndicador;

    @Column(name="nombre")
    private String nombre;

    @Column(name="descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name="id_tipo_ingreso", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_tipoingreso"))
    private TipoIngreso tipoIngreso;

    @ManyToOne
    @JoinColumn(name="id_tipo_valor_meta", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_tipovalormeta"))
    private TipoValorMeta tipoValorMeta;

    @Column(name="valor_meta")
    private int valorMeta;

    @Column(name = "estado")
    private boolean estado;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;
    //	@JsonFormat(pattern="MM/dd/yyyy")
    @Column(name = "fecha_creacion")
    private LocalDateTime  fechaCreacion;
    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
