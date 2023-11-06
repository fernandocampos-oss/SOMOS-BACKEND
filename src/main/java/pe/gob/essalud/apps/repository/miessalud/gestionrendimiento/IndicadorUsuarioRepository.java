package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.IndicadorUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoValorMeta;

import java.time.LocalDateTime;
import java.util.List;

public interface IndicadorUsuarioRepository extends JpaRepository<IndicadorUsuario, Integer> {

    @Query(value = "SELECT * from indicador_usuario iu WHERE iu.id_usuario IN :listIdTrabajadores ORDER BY iu.id_indicador_usuario DESC ", nativeQuery = true)
    List<IndicadorUsuario> listarTrabajadoresIndicadoresJefePrincipal(@Param("listIdTrabajadores") List<Long> listIdTrabajadores);

    @Query(value = "SELECT * from indicador_usuario iu WHERE iu.id_usuario=? AND iu.id_estado_indicador in (1,2)  ORDER BY iu.id_indicador_usuario DESC ", nativeQuery = true)
    List<IndicadorUsuario> listarIndicadoresPendientesPorUsuario(@Param("idUsuario") Number idUsuario);

    @Query(value = "SELECT * from indicador_usuario iu WHERE iu.id_usuario=? AND iu.id_estado_indicador =4  ORDER BY iu.id_indicador_usuario DESC ", nativeQuery = true)
    List<IndicadorUsuario> listarIndicadoresFinalizadoPorUsuario(@Param("idUsuario") Number idUsuario);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO indicador_usuario(id_indicador, cod_red, cod_unidad, id_usuario, id_estado_indicador, fecha_creacion, anio_registro_indicador, peso_total, estado) VALUES (:idIndicador, :codRed, :codUnidad, :idUsuario, :idEstadoIndicador, :fechaCreacion, :anioRegistroIndicador, :pesoTotal, true)", nativeQuery = true)
    Integer registrarIndicadorUsuario(@Param("idIndicador") Integer idIndicador,
                                      @Param("codRed") String codRed,
                                      @Param("codUnidad") String codUnidad,
                                      @Param("idUsuario") Number idUsuario,
                                      @Param("idEstadoIndicador") Number idEstadoIndicador,
                                      @Param("fechaCreacion") LocalDateTime fechaCreacion,
                                      @Param("anioRegistroIndicador") Number anioRegistroIndicador,
                                      @Param("pesoTotal") Number pesoTotal );

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE indicador_usuario SET id_estado_indicador = ? WHERE id_indicador_usuario=? ", nativeQuery = true)
//    public int aprobarIndicador(@Param("estado") Number estado, @Param("idIndicadorUsuario") Number idIndicadorUsuario);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE indicador_usuario SET id_estado_requerimiento = ? , motivo= ? WHERE id_indicador_usuario=? ", nativeQuery = true)
//    public int rechazarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

//    @Query(value = "SELECT * from indicador_usuario iu WHERE iu.id_usuario=? ORDER BY iu.id_indicador_usuario DESC ", nativeQuery = true)
//    List<IndicadorUsuario> getlistIndicadoresByIdUsuario(@Param("idUsuario") Number idUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE indicador_usuario SET id_estado_requerimiento =6, fecha_finalizacion=? WHERE id_indicador_usuario =? ", nativeQuery = true)
    public int finalizarTareaAdministrador(@Param("fechaFinalizacion") LocalDateTime fechaFinalizacion, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

    @Query(value = "SELECT * from indicador_usuario iu WHERE iu.anio_registro=? AND iu.id_estado_requerimiento=6 ORDER BY iu.id_indicador_usuario ASC ", nativeQuery = true)
    List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(@Param("anioRegistro") Number anioRegistro);

}