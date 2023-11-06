package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE indicador_usuario SET id_estado_indicador=2, id_actividad = ? WHERE id_indicador_usuario=? ", nativeQuery = true)
    public int actualizarActividad(@Param("idActividad") Number idActividad, @Param("idIndicadorUsuario") Number idIndicadorUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE tarea SET nombre = ? , plazo= ? , usuario_modificacion=?, fecha_modificacion=? WHERE id_tarea=? ", nativeQuery = true)
    public int actualizarTareaAdministrador(@Param("nombre") String nombre,
                                            @Param("plazo") String plazo,
                                            @Param("usuarioModificacion") Number usuarioModificacion,
                                            @Param("fechaModificaion") LocalDateTime fechaModificaion,
                                            @Param("idTarea") Number idTarea);

    @Transactional
    @Modifying
    @Query(value = "UPDATE tarea SET evidencia_descripcion=?, evidencia_ruta_file=?, evidencia_extension_file=?, evidencia_fecha_registro=? WHERE id_tarea=?", nativeQuery = true)
    Integer crearEvidencia(@Param("evidenciaDescripcion") String evidenciaDescripcion,
                           @Param("rutaFile") String rutaFile,
                           @Param("extension") String extension,
                           @Param("evidenciaFechaRegistro") LocalDateTime evidenciaFechaRegistro,
                           @Param("idTarea") Number idTarea);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE requerimiento SET porcentaje_avance = ? WHERE id_requerimiento=? ", nativeQuery = true)
//    public int actualizaPorcentajeAvanceRequerimiento(@Param("porcentajeAvance") Number porcentajeAvance, @Param("idRequerimiento") Number idRequerimiento);


//    @Query("SELECT r FROM Indicador r WHERE r.idIndicador = :idIndicador")
//    Indicador getByIdRequerimiento(@Param("idIndicador") Integer idIndicador);


}

