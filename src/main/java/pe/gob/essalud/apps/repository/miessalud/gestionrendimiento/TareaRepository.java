package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    @Modifying
    @Query(value = "INSERT INTO tarea(nombre_tarea, plazo, id_requerimiento_usuario, estado, usuario_creacion, fecha_creacion) VALUES (:nombreTarea, :plazo, :idRequerimientoUsuario, true, :usuarioCreacion, :fechaCreacion)", nativeQuery = true)
    Integer registrarTarea(@Param("nombreTarea") String nombreTarea,
                           @Param("plazo") String plazo,
                           @Param("idRequerimientoUsuario") Integer idRequerimientoUsuario,
                           @Param("usuarioCreacion") Number usuarioCreacion,
                           @Param("fechaCreacion") LocalDateTime fechaCreacion);

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

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento SET porcentaje_avance = ? WHERE id_requerimiento=? ", nativeQuery = true)
    public int actualizaPorcentajeAvanceRequerimiento(@Param("porcentajeAvance") Number porcentajeAvance, @Param("idRequerimiento") Number idRequerimiento);

    @Query("SELECT p FROM Poi p ORDER BY p.descripcion ASC")
    List<Poi> listarAllPoi();

    @Query("SELECT ti FROM TipoIngreso ti ORDER BY ti.descripcion ASC")
    List<TipoIngreso> listarAllTipoIngreso();

    @Query("SELECT r FROM Requerimiento r WHERE r.idRequerimiento = :idRequerimiento")
    Requerimiento getByIdRequerimiento(@Param("idRequerimiento") Integer idRequerimiento);

}

