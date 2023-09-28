package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;

import java.time.LocalDateTime;
import java.util.List;

public interface RequerimientoUsuarioRepository extends JpaRepository<RequerimientoUsuario, Integer> {

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario IN :listIdIntegrantes ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<RequerimientoUsuario> listarRequerimientosIntegrantesPrincipal(@Param("listIdIntegrantes") List<Long> listIdIntegrantes);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? AND ru.id_estado_requerimiento in (1,2)  ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<RequerimientoUsuario> listarRequerimientosPendientesPorUsuario(@Param("idUsuario") Number idUsuario);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? AND ru.id_estado_requerimiento=6  ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<RequerimientoUsuario> listarRequerimientosFinalizadoPorUsuario(@Param("idUsuario") Number idUsuario);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? AND ru.id_estado_requerimiento=5  ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<RequerimientoUsuario> listarRequerimientosRechazadoPorUsuario(@Param("idUsuario") Number idUsuario);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO requerimiento_usuario(id_requerimiento, id_usuario, id_estado_requerimiento, cod_red, cod_unidad_solicitante, fecha_creacion, es_jefe) VALUES (:idRequerimiento, :idUsuario, :idEstadoRequerimiento, :codRed, :codUnidadOrganizacion, :fechaCreacion, :esJefe)", nativeQuery = true)
    Integer registrarRequerimientoUsuario(@Param("idRequerimiento") Integer idRequerimiento,
                                          @Param("idUsuario") Number idUsuario,
                                          @Param("idEstadoRequerimiento") Number idEstadoRequerimiento,
                                          @Param("codRed") String codRed,
                                          @Param("codUnidadOrganizacion") String codUnidadOrganizacion,
                                          @Param("fechaCreacion") LocalDateTime fechaCreacion,
                                          @Param("esJefe") boolean esJefe);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento = ? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
    public int aprobarRequerimiento(@Param("estado") Number estado, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento = ? , motivo= ? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
    public int rechazarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento = ? , motivo= ? , cod_unidad_receptor= ? WHERE id_requerimiento_usuario=? ", nativeQuery = true)
    public int derivarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("codUnidadReceptor") String codUnidadReceptor, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

    @Query(value = "SELECT * from requerimiento_usuario ru WHERE ru.id_usuario=? ORDER BY ru.id_requerimiento_usuario DESC ", nativeQuery = true)
    List<RequerimientoUsuario> listarRequerimientosPorPersonal(@Param("idUsuario") Number idUsuario);

    @Query(value = "SELECT u.nombres as nombres, u.apellidos as apellidos, u.id_usuario as idUsuario FROM usuario u WHERE u.cod_red=? AND u.es_activo=TRUE ORDER BY u.nombres ASC ", nativeQuery = true)
    List<PersonalDTO> listarPersonalPorRed(@Param("codRed") String codRed);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento_usuario SET id_estado_requerimiento = 6 WHERE id_requerimiento_usuario=? ", nativeQuery = true)
    public int finalizarTareaAdministrador(@Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

}