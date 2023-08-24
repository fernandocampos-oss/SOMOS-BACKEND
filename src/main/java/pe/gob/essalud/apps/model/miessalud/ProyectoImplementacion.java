package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "proyecto_implementacion")
public class ProyectoImplementacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto_implementacion")
    private Integer idProyectoImplementacion;
    @Column(name = "resultado")
    private String resultado;
    @Column(name = "sostenible")
    private String sostenible;
    @Column(name = "sostenible_fundamento")
    private String sostenibleFundamento;
    @Column(name = "replicable")
    private String replicable;
    @Column(name = "replicable_fundamento")
    private String replicableFundamento;
    @Column(name = "tecnologia")
    private String tecnologia;
    @Column(name = "tecnologia_fundamento")
    private String tecnologiaFundamento;
    @Column(name = "beneficio")
    private String beneficio;
    @Column(name = "enfoque")
    private String enfoque;
    @Column(name = "ruta_archivo")
    private String rutaArchivo;
    @OneToOne
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

}
