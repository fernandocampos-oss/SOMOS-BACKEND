package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    @Query(value = "SELECT * from tarea t WHERE t.id_requerimiento_personal=? AND t.estado=true ORDER BY t.id_tarea DESC", nativeQuery = true)
    List<Tarea> listarTareaPorRequermientoPersonal(@Param("idRequerimientoPersonal") Number idRequerimientoPersonal);

    @Modifying
    @Query(value = "INSERT INTO tarea(nombre_tarea, plazo, id_requerimiento_personal, fecha_creacion, id_estado_tarea, porcentaje_avance, estado) VALUES (:nombreTarea, :plazo, :idRequerimientoPersonal, :fechaCreacion, :idEstadoTarea, :porcentajeAvance, true)", nativeQuery = true)
    Integer registrarTareaNoDuplicado(@Param("nombreTarea") String nombreTarea,
                                      @Param("plazo") String plazo,
                                      @Param("idRequerimientoPersonal") Integer idRequerimientoPersonal,
                                      @Param("fechaCreacion") LocalDateTime fechaCreacion,
                                      @Param("idEstadoTarea") Number idEstadoTarea,
                                      @Param("porcentajeAvance") Integer porcentajeAvance);

    @Query(value = "SELECT * from tarea t WHERE t.id_requerimiento_personal=(select rp.id_requerimiento_personal from requerimiento_personal rp WHERE rp.id_personal=?) ORDER BY t.id_requerimiento_personal DESC", nativeQuery = true)
    List<Tarea> listarTareaPorPersonal(@Param("idPersonal") Number idPersonal);
}

