package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "pago_historial_actividad")
public class PagoHistorialActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_historial")
    private Integer idPagoHistorial;
    @Column(name = "accion")
    private String accion;
    @Column(name = "tipo_accion")
    private Integer tipoAccion;
    @Column(name = "detalle")
    private String detalle;
    @Column(name = "tipo_boleta")
    private String tipoBoleta;
    @Column(name = "usuario_creacion", nullable = false)
    private Integer usuarioCreacion;
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }
}