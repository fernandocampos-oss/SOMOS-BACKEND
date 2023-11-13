package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Prioridad;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PrioridadRepository extends JpaRepository<Prioridad, Integer> {

    @Query("SELECT i from Indicador i WHERE i.esAsignado=false ")
    List<Indicador> getAllIndicadorOrganizar();

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE prioridad SET id_actividad = ? WHERE id_prioridad=? ", nativeQuery = true)
//    public int actualizarPrioridad(@Param("idActividad") Number idActividad, @Param("idPrioridad") Number idPrioridad);

    @Transactional
    @Modifying
    @Query(value = "UPDATE indicador SET id_prioridad=:idPrioridad, es_asignado=TRUE WHERE id_indicador IN :listIdIndicadores ", nativeQuery = true)
    public void actualizarPrioridadEnListaIndicadores(@Param("idPrioridad") Number idPrioridad, @Param("listIdIndicadores") int[] listIdIndicadores);

    @Transactional
    @Modifying
    @Query(value = "UPDATE prioridad SET peso = ? WHERE id_prioridad=? ", nativeQuery = true)
    public int asignarPesoPrioridad(@Param("peso") Number peso, @Param("idPrioridad") Number idPrioridad);

    @Query(value = "SELECT * from prioridad p WHERE p.id_prioridad IN (SELECT DISTINCT id_prioridad from indicador i WHERE i.id_usuario=?) ", nativeQuery = true)
    List<Prioridad> getListIdPrioridadesByTrabajador(@Param("idTrabajador") Number idTrabajador);







////    @Transactional
////    @Modifying
////    @Query(value = "INSERT INTO indicador_usuario(id_indicador, cod_red, cod_unidad, id_usuario, id_estado_indicador, fecha_creacion, anio_registro_indicador, peso_total, estado) VALUES (:idIndicador, :codRed, :codUnidad, :idUsuario, :idEstadoIndicador, :fechaCreacion, :anioRegistroIndicador, :pesoTotal, true)", nativeQuery = true)
////    Integer registrarIndicadorUsuario(@Param("idIndicador") Integer idIndicador,
////                                      @Param("codRed") String codRed,
////                                      @Param("codUnidad") String codUnidad,
////                                      @Param("idUsuario") Number idUsuario,
////                                      @Param("idEstadoIndicador") Number idEstadoIndicador,
////                                      @Param("fechaCreacion") LocalDateTime fechaCreacion,
////                                      @Param("anioRegistroIndicador") Number anioRegistroIndicador,
////                                      @Param("pesoTotal") Number pesoTotal );
//
////    @Transactional
////    @Modifying
////    @Query(value = "UPDATE indicador_usuario SET id_estado_indicador = ? WHERE id_indicador_usuario=? ", nativeQuery = true)
////    public int aprobarIndicador(@Param("estado") Number estado, @Param("idIndicadorUsuario") Number idIndicadorUsuario);
//
////    @Transactional
////    @Modifying
////    @Query(value = "UPDATE indicador_usuario SET id_estado_requerimiento = ? , motivo= ? WHERE id_indicador_usuario=? ", nativeQuery = true)
////    public int rechazarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);
//
////    @Query(value = "SELECT * from indicador_usuario iu WHERE iu.id_usuario=? ORDER BY iu.id_indicador_usuario DESC ", nativeQuery = true)
////    List<IndicadorUsuario> getlistIndicadoresByIdUsuario(@Param("idUsuario") Number idUsuario);
//
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE indicador_usuario SET id_estado_requerimiento =6, fecha_finalizacion=? WHERE id_indicador_usuario =? ", nativeQuery = true)
//    public int finalizarTareaAdministrador(@Param("fechaFinalizacion") LocalDateTime fechaFinalizacion, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);
//
//    @Query(value = "SELECT * from indicador_usuario iu WHERE iu.anio_registro=? AND iu.id_estado_requerimiento=6 ORDER BY iu.id_indicador_usuario ASC ", nativeQuery = true)
//    List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(@Param("anioRegistro") Number anioRegistro);


}
