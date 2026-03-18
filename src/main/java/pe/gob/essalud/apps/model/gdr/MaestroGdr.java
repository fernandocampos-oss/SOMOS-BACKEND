package pe.gob.essalud.apps.model.gdr;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maestro_gdr")
public class MaestroGdr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maestro_gdr")
    private Long idMaestroGdr;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 15)
    private String numeroDocumento;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "usuario_creacion")
    private Integer usuarioCreacion;

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
    public MaestroGdr() {
    }

    public MaestroGdr(String numeroDocumento, Boolean estado, Integer usuarioCreacion) {
        this.numeroDocumento = numeroDocumento;
        this.estado = estado;
        this.usuarioCreacion = usuarioCreacion;
    }

    // Getters y Setters
    public Long getIdMaestroGdr() {
        return idMaestroGdr;
    }

    public void setIdMaestroGdr(Long idMaestroGdr) {
        this.idMaestroGdr = idMaestroGdr;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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

    public Integer getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(Integer usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    @Override
    public String toString() {
        return "MaestroGdr{" +
                "idMaestroGdr=" + idMaestroGdr +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", estado=" + estado +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaModificacion=" + fechaModificacion +
                ", usuarioCreacion=" + usuarioCreacion +
                '}';
    }
}
