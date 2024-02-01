package pe.gob.essalud.apps.model.miessalud;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Entity
@Table(name = "usuario")
@SQLDelete(sql = "UPDATE usuario SET es_activo = false WHERE id_usuario = ?")
@Where(clause = "es_activo = true")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private long idUsuario;
    @Column(name = "numero_documento")
    private String numeroDocumento;
    @Column(name = "codigo_planilla")
    private String codigoPlanilla;
    @Column(name = "nombres")
    private String nombres;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "sexo")
    private String sexo;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column(name = "numero_celular")
    private String numeroCelular;
    @Column(name = "correo")
    private String correo;
    @Column(name = "regimen")
    private String regimen;
    @Column(name = "cargo")
    private String cargo;
    @Column(name = "fecha_ingreso")
    private String fechaIngreso;
    @Column(name = "cod_red")
    private String codigoRed;
    @Column(name = "cod_unidad")
    private String codigoUnidad;
    @Column(name = "es_activo")
    private boolean esActivo;
    @Column(name = "id_rol")
    private int idRol;
    @Column(name = "password")
    private String password;
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    @Column(name = "usuario_modificacion")
    private Integer usuarioModificacion;
    @Column(name = "id_estado_usuario")
    private String idEstadoUsuario;
    @Column(name = "id_rol_adicional")
    private Integer idRolAdicional;
    @Column(name = "ruta_imagen_perfil")
    private String rutaImagenPerfil;
    @Column(name = "ruta_imagen_firma")
    private String rutaImagenFirma;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

    @PreUpdate
    private void preUpdate() {
        this.fechaModificacion = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

}
