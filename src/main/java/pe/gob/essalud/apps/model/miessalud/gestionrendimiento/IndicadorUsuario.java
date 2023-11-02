package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name="indicador_usuario")
public class IndicadorUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_indicador_usuario")
    private Integer idIndicadorUsuario;

    @Column(name="cod_red")
    private String codRed;

    @Column(name="cod_unidad")
    private String codUnidad;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false, foreignKey = @ForeignKey(name="fk_indicador_usuario"))
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="id_indicador", nullable = false, foreignKey = @ForeignKey(name="fk_indicadorusuario_indicador"))
    private Indicador indicador;

    @ManyToOne
    @JoinColumn(name="id_estado_indicador", nullable = false, foreignKey = @ForeignKey(name="fk_indicadorusuario_estadoindicador"))
    private EstadoIndicador estadoIndicador;

    @ManyToOne
    @JoinColumn(name="id_actividad", nullable = true, foreignKey = @ForeignKey(name="fk_indicadorusuario_actividad"))
    private Actividad actividad;

    @Column(name = "fecha_finalizado_indicador")
    private LocalDateTime fechaFinalizadoIndicador;

    @Column(name = "anio_registro_indicador")
    private int  anioRegistroIndicador;

    @Column(name = "estado")
    private boolean estado;

    @OneToMany(mappedBy = "indicadorUsuario", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Tarea> listTarea;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idIndicadorUsuario == null) ? 0 : idIndicadorUsuario.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IndicadorUsuario other = (IndicadorUsuario) obj;
        if (idIndicadorUsuario == null) {
            if (other.idIndicadorUsuario != null)
                return false;
        } else if (!idIndicadorUsuario.equals(other.idIndicadorUsuario))
            return false;
        return true;
    }

}
