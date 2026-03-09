package pe.gob.essalud.apps.model.gdr;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para controlar las fases y evidencias del ciclo GDR por periodo.
 * Esta configuración es GLOBAL y aplica a todos los evaluadores del periodo.
 */
@Entity
@Table(name = "configuracion_gdr")
public class ConfiguracionGdr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @Column(name = "periodo", nullable = false, unique = true, length = 4)
    private String periodo;

    // Control de FASES
    @Column(name = "fase_pre_activa", nullable = false)
    private Boolean fasePreActiva = false;

    @Column(name = "fase_planificacion_activa", nullable = false)
    private Boolean fasePlanificacionActiva = true;

    @Column(name = "fase_seguimiento_activa", nullable = false)
    private Boolean faseSeguimientoActiva = false;

    @Column(name = "fase_evaluacion_activa", nullable = false)
    private Boolean faseEvaluacionActiva = false;

    @Column(name = "fase_post_activa", nullable = false)
    private Boolean fasePostActiva = false;

    // Control de EVIDENCIAS
    @Column(name = "evidencia_1_activa", nullable = false)
    private Boolean evidencia1Activa = false;

    @Column(name = "evidencia_2_activa", nullable = false)
    private Boolean evidencia2Activa = false;

    @Column(name = "evidencia_final_activa", nullable = false)
    private Boolean evidenciaFinalActiva = false;

    // Auditoría
    @Column(name = "modificado_por", length = 15)
    private String modificadoPor;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    // Constructores
    public ConfiguracionGdr() {
    }

    public ConfiguracionGdr(String periodo) {
        this.periodo = periodo;
    }

    // Getters y Setters
    public Long getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(Long idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Boolean getFasePreActiva() {
        return fasePreActiva;
    }

    public void setFasePreActiva(Boolean fasePreActiva) {
        this.fasePreActiva = fasePreActiva;
    }

    public Boolean getFasePlanificacionActiva() {
        return fasePlanificacionActiva;
    }

    public void setFasePlanificacionActiva(Boolean fasePlanificacionActiva) {
        this.fasePlanificacionActiva = fasePlanificacionActiva;
    }

    public Boolean getFaseSeguimientoActiva() {
        return faseSeguimientoActiva;
    }

    public void setFaseSeguimientoActiva(Boolean faseSeguimientoActiva) {
        this.faseSeguimientoActiva = faseSeguimientoActiva;
    }

    public Boolean getFaseEvaluacionActiva() {
        return faseEvaluacionActiva;
    }

    public void setFaseEvaluacionActiva(Boolean faseEvaluacionActiva) {
        this.faseEvaluacionActiva = faseEvaluacionActiva;
    }

    public Boolean getFasePostActiva() {
        return fasePostActiva;
    }

    public void setFasePostActiva(Boolean fasePostActiva) {
        this.fasePostActiva = fasePostActiva;
    }

    public Boolean getEvidencia1Activa() {
        return evidencia1Activa;
    }

    public void setEvidencia1Activa(Boolean evidencia1Activa) {
        this.evidencia1Activa = evidencia1Activa;
    }

    public Boolean getEvidencia2Activa() {
        return evidencia2Activa;
    }

    public void setEvidencia2Activa(Boolean evidencia2Activa) {
        this.evidencia2Activa = evidencia2Activa;
    }

    public Boolean getEvidenciaFinalActiva() {
        return evidenciaFinalActiva;
    }

    public void setEvidenciaFinalActiva(Boolean evidenciaFinalActiva) {
        this.evidenciaFinalActiva = evidenciaFinalActiva;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public String toString() {
        return "ConfiguracionGdr{" +
                "idConfiguracion=" + idConfiguracion +
                ", periodo='" + periodo + '\'' +
                ", fasePlanificacionActiva=" + fasePlanificacionActiva +
                ", faseSeguimientoActiva=" + faseSeguimientoActiva +
                ", faseEvaluacionActiva=" + faseEvaluacionActiva +
                ", evidencia1Activa=" + evidencia1Activa +
                ", evidencia2Activa=" + evidencia2Activa +
                ", evidenciaFinalActiva=" + evidenciaFinalActiva +
                '}';
    }
}
