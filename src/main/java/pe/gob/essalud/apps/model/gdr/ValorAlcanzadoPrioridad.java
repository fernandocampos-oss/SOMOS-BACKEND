package pe.gob.essalud.apps.model.gdr;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "valor_alcanzado_prioridad", schema = "public")
public class ValorAlcanzadoPrioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_prioridad", nullable = false, unique = true)
    private Long idPrioridad;

    @Column(name = "valor_alcanzado", precision = 10, scale = 2)
    private BigDecimal valorAlcanzado;

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
    public ValorAlcanzadoPrioridad() {
    }

    public ValorAlcanzadoPrioridad(Long idPrioridad, BigDecimal valorAlcanzado) {
        this.idPrioridad = idPrioridad;
        this.valorAlcanzado = valorAlcanzado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPrioridad() {
        return idPrioridad;
    }

    public void setIdPrioridad(Long idPrioridad) {
        this.idPrioridad = idPrioridad;
    }

    public BigDecimal getValorAlcanzado() {
        return valorAlcanzado;
    }

    public void setValorAlcanzado(BigDecimal valorAlcanzado) {
        this.valorAlcanzado = valorAlcanzado;
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
