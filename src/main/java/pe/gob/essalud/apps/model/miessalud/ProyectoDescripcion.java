package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "proyecto_descripcion")
public class ProyectoDescripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto_descripcion")
    private Integer idProyectoDescripcion;
    @Column(name = "fecha")
    private String fecha;
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "contexto")
    private String contexto;
    @Column(name = "innovacion")
    private String innovacion;
    @Column(name = "indicador")
    private String indicador;
    @OneToOne
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

}
