package pe.gob.essalud.apps.model.gdr;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad para registrar la asistencia a la Reunión de Establecimiento de Metas
 * entre evaluador y evaluado.
 */
@Entity
@Table(name = "reunion_establecimiento_metas",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"id_votante_evaluado", "id_votante_evaluador", "periodo"}
       ))
public class ReunionEstablecimientoMetas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reunion")
    private Long idReunion;

    @Column(name = "id_votante_evaluado", nullable = false)
    private Long idVotanteEvaluado;

    @Column(name = "id_votante_evaluador", nullable = false)
    private Long idVotanteEvaluador;

    @Column(name = "periodo", nullable = false, length = 4)
    private String periodo;

    /**
     * Estado de asistencia:
     * '-' = No seleccionado (estado inicial)
     * 'S' = Sí asistió
     * 'N' = No asistió
     */
    @Column(name = "asistio", nullable = false, length = 1)
    private String asistio = "-";

    @Column(name = "fecha_reunion")
    private LocalDate fechaReunion;

    @Column(name = "confirmado", nullable = false)
    private Boolean confirmado = false;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "reiniciado_por", length = 15)
    private String reiniciadoPor;

    @Column(name = "fecha_reinicio")
    private LocalDateTime fechaReinicio;

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
    public ReunionEstablecimientoMetas() {
    }

    public ReunionEstablecimientoMetas(Long idVotanteEvaluado, Long idVotanteEvaluador, String periodo) {
        this.idVotanteEvaluado = idVotanteEvaluado;
        this.idVotanteEvaluador = idVotanteEvaluador;
        this.periodo = periodo;
        this.asistio = "-";
        this.confirmado = false;
    }

    // Getters y Setters
    public Long getIdReunion() {
        return idReunion;
    }

    public void setIdReunion(Long idReunion) {
        this.idReunion = idReunion;
    }

    public Long getIdVotanteEvaluado() {
        return idVotanteEvaluado;
    }

    public void setIdVotanteEvaluado(Long idVotanteEvaluado) {
        this.idVotanteEvaluado = idVotanteEvaluado;
    }

    public Long getIdVotanteEvaluador() {
        return idVotanteEvaluador;
    }

    public void setIdVotanteEvaluador(Long idVotanteEvaluador) {
        this.idVotanteEvaluador = idVotanteEvaluador;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getAsistio() {
        return asistio;
    }

    public void setAsistio(String asistio) {
        this.asistio = asistio;
    }

    public LocalDate getFechaReunion() {
        return fechaReunion;
    }

    public void setFechaReunion(LocalDate fechaReunion) {
        this.fechaReunion = fechaReunion;
    }

    public Boolean getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(Boolean confirmado) {
        this.confirmado = confirmado;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public String getReiniciadoPor() {
        return reiniciadoPor;
    }

    public void setReiniciadoPor(String reiniciadoPor) {
        this.reiniciadoPor = reiniciadoPor;
    }

    public LocalDateTime getFechaReinicio() {
        return fechaReinicio;
    }

    public void setFechaReinicio(LocalDateTime fechaReinicio) {
        this.fechaReinicio = fechaReinicio;
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

    /**
     * Método de utilidad para verificar si se puede confirmar
     */
    public boolean puedeConfirmar() {
        return !"-".equals(this.asistio) && !this.confirmado;
    }

    /**
     * Método de utilidad para verificar si ya está confirmado
     */
    public boolean estaConfirmado() {
        return Boolean.TRUE.equals(this.confirmado);
    }

    @Override
    public String toString() {
        return "ReunionEstablecimientoMetas{" +
                "idReunion=" + idReunion +
                ", idVotanteEvaluado=" + idVotanteEvaluado +
                ", idVotanteEvaluador=" + idVotanteEvaluador +
                ", periodo='" + periodo + '\'' +
                ", asistio='" + asistio + '\'' +
                ", confirmado=" + confirmado +
                '}';
    }
}
