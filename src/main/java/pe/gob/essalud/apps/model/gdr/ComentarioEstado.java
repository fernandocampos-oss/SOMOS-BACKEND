package pe.gob.essalud.apps.model.gdr;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario_estado", schema = "public")
public class ComentarioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_evidencia", nullable = false, unique = true)
    private Long idEvidencia;

    @Column(name = "estado_dropdown", length = 50)
    private String estadoDropdown; // 'logrado', 'proceso', 'no_presento'

    @Column(name = "comentario_adicional", columnDefinition = "TEXT")
    private String comentarioAdicional;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    // Constructores
    public ComentarioEstado() {
    }

    public ComentarioEstado(Long idEvidencia, String estadoDropdown, String comentarioAdicional) {
        this.idEvidencia = idEvidencia;
        this.estadoDropdown = estadoDropdown;
        this.comentarioAdicional = comentarioAdicional;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEvidencia() {
        return idEvidencia;
    }

    public void setIdEvidencia(Long idEvidencia) {
        this.idEvidencia = idEvidencia;
    }

    public String getEstadoDropdown() {
        return estadoDropdown;
    }

    public void setEstadoDropdown(String estadoDropdown) {
        this.estadoDropdown = estadoDropdown;
    }

    public String getComentarioAdicional() {
        return comentarioAdicional;
    }

    public void setComentarioAdicional(String comentarioAdicional) {
        this.comentarioAdicional = comentarioAdicional;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}
