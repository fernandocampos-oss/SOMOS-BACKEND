package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;

import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo,Integer> {

    @Query("SELECT e FROM Equipo e WHERE e.usuarioCreacion = :idLider AND e.esActivo=TRUE ORDER BY e.idEquipo DESC ")
    List<Equipo> getListTrabajadoresByIdUsuarioJefe(@Param("idLider") Number idLider);

    @Query("SELECT v FROM Votante v WHERE v.idUsuario = :idUsuario")
    Votante getVotanteByIdUsuario(@Param("idUsuario") Integer idUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE equipo SET es_activo = FALSE WHERE id_equipo=? ", nativeQuery = true)
    public int eliminarTrabajador(@Param("idEquipo") Number idEquipo);

    @Query(value = "SELECT v.nombres as nombres, v.apellidos as apellidos, v.id_votante as idVotante FROM votante v ORDER BY v.nombres ASC ", nativeQuery = true)
    List<PersonalDTO> listAllVotante();

//    @Query("SELECT u FROM Usuario u WHERE u.numeroDocumento=:numeroDocumento AND u.esActivo=TRUE ")
//    Usuario findUsuarioByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

}
