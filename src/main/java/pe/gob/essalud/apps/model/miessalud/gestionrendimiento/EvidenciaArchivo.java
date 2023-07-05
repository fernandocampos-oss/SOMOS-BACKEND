package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "evidencia_archivo")
public class EvidenciaArchivo {

    @Id
    @Column(name = "id_evidencia_archivo")
    private Integer idEvidenciaArchivo;

    @Column(name = "estado", nullable = true)
    private boolean estado;

    @ManyToOne
    @JoinColumn(name="id_evidencia", nullable = true, foreignKey = @ForeignKey(name="fk_evidenciaarchivo_evidencia"))
    private Evidencia evidencia;

    @ManyToOne
    @JoinColumn(name="id_archivo", nullable = true, foreignKey = @ForeignKey(name="fk_evidenciaarchivo_archivo"))
    private Archivo archivo;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "id_usuario_modificacion")
    private Integer idUsuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;
}
