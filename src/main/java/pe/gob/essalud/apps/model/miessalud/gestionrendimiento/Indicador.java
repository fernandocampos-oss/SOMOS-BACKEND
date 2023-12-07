package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.gob.essalud.apps.model.miessalud.Votante;

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

    @Column(name="descripcion")
    private String descripcion;

    @Column(name="peso")
    private int peso;

    @ManyToOne
    @JoinColumn(name="id_votante", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_votante"))
    private Votante votante;

    @ManyToOne
    @JoinColumn(name="id_tipo_valor_meta", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_tipovalormeta"))
    private TipoValorMeta tipoValorMeta;

    @Column(name="valor_meta")
    private int valorMeta;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_prioridad", nullable = true, foreignKey = @ForeignKey(name="fk_indicador_prioridad"))
    private Prioridad prioridad;

    @Column(name="cod_red")
    private String codRed;

    @Column(name="cod_unidad")
    private String codUnidad;

    @Column(name="anio")
    private int anio;

    @Column(name = "estado")
    private boolean estado;

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

    @PreUpdate
    private void preUpdate() {
        this.fechaModificacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }
}
