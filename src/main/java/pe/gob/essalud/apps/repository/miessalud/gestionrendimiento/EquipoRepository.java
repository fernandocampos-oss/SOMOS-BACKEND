package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.TrabajadorResponseDto;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;

import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Integer> {

    @Query("SELECT e FROM Equipo e WHERE e.usuarioCreacion = :idJefe AND e.esActivo=TRUE ORDER BY e.idEquipo DESC ")
    List<Equipo> getListTrabajadoresByIdUsuarioJefe(@Param("idJefe") Number idJefe);

    @Transactional
    @Modifying
    @Query(value = "UPDATE equipo SET es_activo = FALSE WHERE id_equipo=? ", nativeQuery = true)
    public int eliminarTrabajador(@Param("idEquipo") Number idEquipo);

    @Query(value = "SELECT v.nombres as nombres, v.apellidos as apellidos, v.id_votante as idVotante FROM votante v WHERE v.id_usuario !=:idJefe AND v.id_usuario IS NOT NULL ", nativeQuery = true)
    List<TrabajadorResponseDto> listAllVotante(@Param("idJefe") Number idJefe);

    @Query("SELECT v FROM Votante v WHERE v.idUsuario = :idUsuario")
    Votante getVotanteByIdUsuario(@Param("idUsuario") Integer idUsuario);

    @Query(value = "SELECT * from equipo e WHERE e.id_integrante=?  LIMIT 1", nativeQuery = true)
    Equipo getJefeByIdIntegrante(@Param("idVotante") Integer idVotante);
}
