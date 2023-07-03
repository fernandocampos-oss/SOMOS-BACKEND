package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Personal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface PersonalRepository extends JpaRepository<Personal, Integer>{

    @Query(value = "SELECT * from personal p WHERE p.id_dependencia=? AND p.id_estado_personal=? ORDER BY p.id_personal DESC", nativeQuery = true)
    List<Personal> listarPersonalPorDependenciaAsignado(@Param("idDependencia") Number idDependencia, @Param("idEstadoPersonal") Number idEstadoPersonal);

    @Query(value = "SELECT * from personal p WHERE LOWER(p.nombres) like %:nombres%", nativeQuery = true)
    List<Personal> buscarPersonalPorNombre(@Param("nombres") String nombres);

    @Transactional
    @Modifying
    @Query(value = "UPDATE personal SET id_estado_personal = ?, motivo_eliminado=? WHERE id_personal=? ", nativeQuery = true)
    public int eliminarPersonalMotivo(@Param("idEstadoPersonal") Number idEstadoPersonal, @Param("motivo") String motivo, @Param("idPersonal") Number idPersonal);

}
