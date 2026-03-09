package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.TrabajadorResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.VotantePlanillaResponseDto;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;

import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Integer> {

    @Query("SELECT e FROM Equipo e WHERE e.usuarioCreacion = :idJefe AND e.esActivo=TRUE ORDER BY e.idEquipo DESC ")
    List<Equipo> getListTrabajadoresByIdUsuarioJefe(@Param("idJefe") Number idJefe);

    @Query("SELECT e FROM Equipo e WHERE (e.jefe.idVotante = :idJefe OR e.evaluador = :idJefe) AND e.esActivo=TRUE ORDER BY e.idEquipo DESC ")
    List<Equipo> getListTrabajadoresByIdUsuarioJefeOrEvaluador(@Param("idJefe") Number idJefe);

    @Transactional
    @Modifying
    @Query(value = "UPDATE equipo SET es_activo = FALSE WHERE id_equipo=? ", nativeQuery = true)
    public int eliminarTrabajador(@Param("idEquipo") Number idEquipo);

    @Query(value = "SELECT v.nombres as nombres, v.apellidos as apellidos, v.id_votante as idVotante FROM votante v WHERE v.id_usuario !=:idJefe AND v.id_usuario IS NOT NULL ", nativeQuery = true)
    List<TrabajadorResponseDto> listAllVotante(@Param("idJefe") Number idJefe);

    @Query("SELECT v FROM Votante v WHERE v.idUsuario = :idUsuario")
    Votante getVotanteByIdUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT v FROM Votante v WHERE v.idVotante = :idVotante")
    Votante getVotanteByIdVotante(@Param("idVotante") Integer idVotante);

    @Query("SELECT e.evaluador FROM Equipo e WHERE e.jefe.idVotante = :idJefe AND e.esActivo=TRUE AND e.evaluador != null GROUP BY e.evaluador ")
    Integer getIdVotanteEvaluador(@Param("idJefe") Integer idJefe);

    @Query(value = "SELECT * from equipo e WHERE e.id_integrante=?  LIMIT 1", nativeQuery = true)
    Equipo getJefeByIdIntegrante(@Param("idVotante") Integer idVotante);

    @Query("SELECT v FROM Votante v WHERE v.nombres LIKE %:nombre% ")
    List<Votante> findVotanteByNombre(@Param("nombre") String nombre);

    @Query("SELECT new pe.gob.essalud.apps.dto.gestionrendimiento.response.VotantePlanillaResponseDto(v.idVotante, v.numeroDocumento, v.nombres, v.apellidos, v.idSegmento, v.idUsuario, v.codCondicion, " +
            "(select u.codigoPlanilla from Usuario u where u.idUsuario = v.idUsuario and u.idEstadoUsuario ='02') as codigoPlanilla) " +
            "FROM Votante v WHERE (v.nombres || ' ' || v.apellidos) LIKE %:nombre%")
    List<VotantePlanillaResponseDto> findVotanteByNombre2(@Param("nombre") String nombre);

    @Query(value = "SELECT MAX(id_votante) FROM Votante", nativeQuery = true)
    int getCantidadRegistro();

    @Query(value = "SELECT count(*) from equipo e WHERE e.id_evaluador=? ", nativeQuery = true)
    int getEsEvaluadorDelGrupo(@Param("idEvaluador") Integer idEvaluador);

    @Transactional
    @Modifying
    @Query(value = "UPDATE equipo SET id_evaluador = ? WHERE id_jefe = ? ", nativeQuery = true)
    public int actualizarEvaluador(@Param("idEvaluador") Integer idEvaluador, @Param("idEquipo") Integer idEquipo);

    @Query(value = "SELECT v.nombres as nombres, v.apellidos as apellidos, v.id_votante as idVotante " +
            "FROM votante v " +
            "INNER JOIN usuario u ON v.id_usuario = u.id_usuario " +
            "WHERE v.id_usuario != :idJefe " +
            "AND V.id_segmento <> 1 " +
            "AND v.id_usuario IS NOT NULL " +
            "AND u.cod_unidad IN :codigosUnidad", nativeQuery = true)
    List<TrabajadorResponseDto> listAllVotanteByCodUnidad(
            @Param("idJefe") Number idJefe,
            @Param("codigosUnidad") List<String> codigosUnidad);

    @Query(value = "SELECT v.nombres as nombres, v.apellidos as apellidos, v.id_votante as idVotante " +
            "FROM votante v " +
            "INNER JOIN usuario u ON v.id_usuario = u.id_usuario " +
            "WHERE " +
            "v.id_usuario IS NOT NULL " +
            "AND u.cod_unidad IN :codigosUnidad", nativeQuery = true)
    List<TrabajadorResponseDto> listAllAvalibleEvaluador(
            @Param("codigosUnidad") List<String> codigosUnidad);

    @Query("SELECT v FROM Votante v WHERE v.numeroDocumento = :numeroDocumento")
    Votante getVotanteByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    // Obtener IDs de evaluadores (jefes) que tienen trabajadores asignados activos
    @Query(value = "SELECT DISTINCT e.id_jefe FROM equipo e WHERE e.es_activo = TRUE", nativeQuery = true)
    List<Integer> findJefesConTrabajadores();

    // Contar trabajadores activos por jefe
    @Query(value = "SELECT COUNT(*) FROM equipo e WHERE e.id_jefe = :idJefe AND e.es_activo = TRUE", nativeQuery = true)
    int countTrabajadoresByJefe(@Param("idJefe") Integer idJefe);

    // Obtener trabajadores asignados a un evaluador
    @Query("SELECT e FROM Equipo e WHERE e.jefe.idVotante = :idJefe AND e.esActivo = TRUE ORDER BY e.idEquipo DESC")
    List<Equipo> findTrabajadoresByEvaluador(@Param("idJefe") Integer idJefe);

    // Verificar si un trabajador ya tiene evaluador asignado
    @Query("SELECT e FROM Equipo e WHERE e.integrante.idVotante = :idIntegrante AND e.esActivo = TRUE")
    Equipo findEvaluadorByTrabajador(@Param("idIntegrante") Integer idIntegrante);

    // Verificar si ya existe asignación evaluador-trabajador
    @Query("SELECT e FROM Equipo e WHERE e.jefe.idVotante = :idJefe AND e.integrante.idVotante = :idIntegrante AND e.esActivo = TRUE")
    Equipo findAsignacion(@Param("idJefe") Integer idJefe, @Param("idIntegrante") Integer idIntegrante);

}
