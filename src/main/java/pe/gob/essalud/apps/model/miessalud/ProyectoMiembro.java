package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "proyecto_miembro")
public class ProyectoMiembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto_miembro")
    private Integer idProyectoMiembro;
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Column(name = "numero_documento")
    private String numeroDocumento;
    @Column(name = "codigo_planilla")
    private String codigoPlanilla;
    @Column(name = "cargo")
    private String cargo;
    @Column(name = "nombres")
    private String nombres;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column(name = "numero_celular")
    private String numeroCelular;
    @Column(name = "correo")
    private String correo;
    @Column(name = "red")
    private String red;
    @Column(name = "unidad")
    private String unidad;
    @ManyToOne
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

}
