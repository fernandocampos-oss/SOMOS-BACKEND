package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Personal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalRepository extends JpaRepository<Personal, Integer>{

    @Query(value = "SELECT * from personal p WHERE p.id_dependencia=? AND p.estado=? ORDER BY p.id_personal DESC", nativeQuery = true)
    List<Personal> listarPersonalPorDependenciaAsignado(@Param("idDependencia") Number idDependencia, @Param("estadoAsignado") Character estadoAsignado);

    @Query(value = "SELECT * from personal p WHERE LOWER(p.nombres) like %:nombres%", nativeQuery = true)
    List<Personal> buscarPersonalPorNombre(@Param("nombres") String nombres);

}
