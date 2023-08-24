package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "proyecto_grupo")
public class ProyectoGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto_grupo")
    private Integer idProyectoGrupo;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "sede")
    private String sede;
    @Column(name = "jefe")
    private String jefe;
    @Column(name = "categoria")
    private String categoria;
    @Column(name = "ruta_imagen")
    private String rutaImagen;
    @OneToOne
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

}
