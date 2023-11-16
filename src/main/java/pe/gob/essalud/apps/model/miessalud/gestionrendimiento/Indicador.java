package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor
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

    @Column(name="peso")
    private int peso;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_usuario"))
    private Usuario usuario;

    @Column(name = "es_asignado")
    private boolean esAsignado;

    @Column(name="valor_meta")
    private int valorMeta;

    @ManyToOne
    @JoinColumn(name="id_tipo_ingreso", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_tipoingreso"))
    private TipoIngreso tipoIngreso;

    @ManyToOne
    @JoinColumn(name="id_tipo_valor_meta", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_tipovalormeta"))
    private TipoValorMeta tipoValorMeta;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_prioridad", nullable = true, foreignKey = @ForeignKey(name="fk_indicador_prioridad"))
    private Prioridad prioridad;

    @Column(name="anio_registro")
    private int anioRegistro;

    @Column(name = "estado")
    private boolean estado;

    @ManyToOne
    @JoinColumn(name="id_estado_indicador", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_estadoindicador"))
    private EstadoIndicador estadoIndicador;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;
    //	@JsonFormat(pattern="MM/dd/yyyy")
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
