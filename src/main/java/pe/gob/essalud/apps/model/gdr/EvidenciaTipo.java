package pe.gob.essalud.apps.model.gdr;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidencia_tipo")
public class EvidenciaTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_evidencia", nullable = false, unique = true)
    private Long idEvidencia;

    @Column(name = "id_indicador", nullable = false)
    private Long idIndicador;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // 'inicial' o 'final'

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "fecha_plazo")
    private LocalDate fechaPlazo;

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
    public EvidenciaTipo() {
    }

    public EvidenciaTipo(Long idEvidencia, Long idIndicador, String tipo, Integer orden) {
        this.idEvidencia = idEvidencia;
        this.idIndicador = idIndicador;
        this.tipo = tipo;
        this.orden = orden;
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

    public Long getIdIndicador() {
        return idIndicador;
    }

    public void setIdIndicador(Long idIndicador) {
        this.idIndicador = idIndicador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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

    public LocalDate getFechaPlazo() {
        return fechaPlazo;
    }

    public void setFechaPlazo(LocalDate fechaPlazo) {
        this.fechaPlazo = fechaPlazo;
    }
}
