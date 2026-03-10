package pe.gob.essalud.apps.model.gdr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultados_finales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadosFinales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_votante", nullable = false)
    private Long idVotante;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "rendimiento_distinguido", length = 10)
    private String rendimientoDistinguido;

    @Column(name = "acciones_capacitacion", columnDefinition = "TEXT")
    private String accionesCapacitacion;

    @Column(name = "otras_acciones", columnDefinition = "TEXT")
    private String otrasAcciones;

    @Column(name = "fecha_reunion")
    private LocalDate fechaReunion;

    @Column(name = "permanencia_seis_meses", length = 10)
    private String permanenciaSeisMeses;

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
}
