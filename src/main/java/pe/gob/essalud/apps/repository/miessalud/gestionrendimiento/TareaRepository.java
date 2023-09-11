package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    @Modifying
    @Query(value = "INSERT INTO tarea(nombre_tarea, plazo, id_requerimiento_usuario, estado, usuario_creacion, fecha_creacion, porcentaje_avance) VALUES (:nombreTarea, :plazo, :idRequerimientoUsuario, true, :usuarioCreacion, :fechaCreacion, 0)", nativeQuery = true)
    Integer registrarTarea(@Param("nombreTarea") String nombreTarea,
                           @Param("plazo") String plazo,
                           @Param("idRequerimientoUsuario") Integer idRequerimientoUsuario,
                           @Param("usuarioCreacion") Number usuarioCreacion,
                           @Param("fechaCreacion") LocalDateTime fechaCreacion
//                         @Param("idEstadoTarea") Number idEstadoTarea,
                           );

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento_usuario SET id_poi = ? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
    public int actualizarPoi(@Param("idPoi") Number idPoi, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE tarea SET nombre_tarea = ? , plazo= ? , usuario_modificacion=?, fecha_modificacion=? WHERE id_tarea=? ", nativeQuery = true)
    public int actualizarTareaAdministrador(@Param("nombreTarea") String nombreTarea,
                                            @Param("plazo") String plazo,
                                            @Param("usuarioModificacion") Number usuarioModificacion,
                                            @Param("fechaModificaion") LocalDateTime fechaModificaion,
                                            @Param("idTarea") Number idTarea);

    @Transactional
    @Modifying
    @Query(value = "UPDATE evidencia SET ruta_imagen = ? WHERE id_evidencia=?", nativeQuery = true)
    Integer actualizarRutaImagenEvidencia(@Param("rutaImagen") String rutaImagen, @Param("idEvidencia") Number idEvidencia);

    @Query("SELECT e FROM Evidencia e WHERE e.tarea.idTarea = :idTarea ORDER BY e.fechaCreacion DESC")
    List<Evidencia> listarEvidenciaTarea(@Param("idTarea") Integer idTarea);
}

