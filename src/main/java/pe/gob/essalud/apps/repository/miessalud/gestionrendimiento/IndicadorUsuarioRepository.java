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

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario IN :listIdIntegrantes ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<IndicadorUsuario> listarRequerimientosIntegrantesPrincipal(@Param("listIdIntegrantes") List<Long> listIdIntegrantes);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? AND ru.id_estado_requerimiento in (1,2)  ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<IndicadorUsuario> listarRequerimientosPendientesPorUsuario(@Param("idUsuario") Number idUsuario);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? AND ru.id_estado_requerimiento=6  ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<IndicadorUsuario> listarRequerimientosFinalizadoPorUsuario(@Param("idUsuario") Number idUsuario);

//    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? AND ru.id_estado_requerimiento=5  ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
//    List<RequerimientoUsuario> listarRequerimientosRechazadoPorUsuario(@Param("idUsuario") Number idUsuario);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO indicador_usuario(id_indicador, cod_red, cod_unidad, id_usuario, id_estado_indicador, fecha_creacion, anio_registro_indicador) VALUES (:idIndicador, :codRed, :codUnidad, :idUsuario, :idEstadoIndicador, :fechaCreacion, :anioRegistroIndicador)", nativeQuery = true)
    Integer registrarIndicadorUsuario(@Param("idIndicador") Integer idIndicador,
                                      @Param("codRed") String codRed,
                                      @Param("codUnidad") String codUnidad,
                                      @Param("idUsuario") Number idUsuario,
                                      @Param("idEstadoIndicador") Number idEstadoIndicador,
                                      @Param("fechaCreacion") LocalDateTime fechaCreacion,
                                      @Param("anioRegistroIndicador") Number anioRegistroIndicador);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento = ? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
    public int aprobarRequerimiento(@Param("estado") Number estado, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento = ? , motivo= ? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
//    public int rechazarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento = ? , motivo= ? , cod_unidad_receptor= ? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
//    public int derivarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("codUnidadReceptor") String codUnidadReceptor, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<IndicadorUsuario> listarRequerimientosPorPersonal(@Param("idUsuario") Number idUsuario);
//
//    @Query(value = "SELECT u.nombres as nombres, u.apellidos as apellidos, u.id_usuario as idUsuario FROM usuario u WHERE u.cod_red=? AND u.es_activo=TRUE ORDER BY u.nombres ASC ", nativeQuery = true)
//    List<PersonalDTO> listarPersonalPorRed(@Param("codRed") String codRed);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento =6, fecha_finalizacion=? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
    public int finalizarTareaAdministrador(@Param("fechaFinalizacion") LocalDateTime fechaFinalizacion, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.anio_registro=? AND ru.id_estado_requerimiento=6 ORDER BY ru.id_requerimiento_usuario ASC ", nativeQuery = true)
    List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(@Param("anioRegistro") Number anioRegistro);

//    @Query("SELECT t FROM TipoValorMeta t ORDER BY t.descripcion ASC")
//    List<TipoValorMeta> listarAllTipoValorMeta();
}