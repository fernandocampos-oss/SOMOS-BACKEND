package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "archivo")
public class Archivo {

    @Id
    @Column(name = "id_archivo")
    private Integer idArchivo;

    @Column(name="nombre", nullable = false)
    private String nombre;

    @Column(name="nombre_registro", nullable = false)
    private String nombreRegistro;

    @Column(name="ruta", nullable = true)
    private String ruta;

    @Column(name="tipo", nullable = true)
    private String tipo;

    @Column(name="extension", nullable = true)
    private String extension;

    @Column(name = "estado", nullable = true)
    private boolean estado;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "id_usuario_modificacion")
    private Integer idUsuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime  fechaModificacion;
}

