package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.LiderEquipo;

import java.util.List;

public interface LiderEquipoRepository extends JpaRepository<LiderEquipo,Integer> {

    @Query("SELECT le FROM LiderEquipo le WHERE le.lider.idUsuario = :idLider AND le.esActivo=TRUE ORDER BY le.idLiderEquipo DESC ")
    List<LiderEquipo> listarIntegrantesPorLider(@Param("idLider") Long idLider);

    @Transactional
    @Modifying
    @Query(value = "UPDATE lider_equipo SET es_activo = false WHERE id_integrante=? ", nativeQuery = true)
    public int eliminarIntegrante(@Param("idIntegrante") Number idIntegrante);

}
