package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoPersonal;

import java.util.List;

public interface RequerimientoPersonalRepository extends JpaRepository<RequerimientoPersonal, Integer> {

    @Query(value = "SELECT * from requerimiento_personal rp WHERE rp.id_personal=? ORDER BY rp.id_requerimiento_personal DESC ", nativeQuery = true)
    List<RequerimientoPersonal> listarRequerimientosPorPersonal(@Param("idPersonal") Number idPersonal);

    @Query(value = "SELECT * FROM requerimiento_personal rp WHERE rp.id_requerimiento=? AND rp.id_personal=? ", nativeQuery = true)
    List<RequerimientoPersonal> validarDuplicadoRequerimientoPersonal(@Param("idRequerimiento") Number idRequerimiento, @Param("idPersonal") Number idPersonal);

}

