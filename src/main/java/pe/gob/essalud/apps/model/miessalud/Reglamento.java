package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
@Getter
@Setter
@Entity
@Table(name = "reglamento")
public class Reglamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reglamento")
    private Integer idReglamento;
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Column(name = "codigo_planilla")
    private String codigoPlanilla;
    @Column(name = "numero_documento")
    private String numeroDocumento;
    @Column(name = "nombres")
    private String nombres;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "item_politica")
    private String itemPolitica;
    @Column(name = "item_reglamento")
    private String itemReglamento;
    @Column(name = "item_recomendacion")
    private String itemRecomendacion;
    @Column(name = "primer_semestre")
    private Integer primerSemestre;
    @Column(name = "segundo_semestre")
    private Integer segundoSemestre;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "red")
    private String red;
    @Column(name = "unidad")
    private String unidad;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_apertura_usuario")
    private LocalDateTime fechaAperturaUsuario;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }
}
