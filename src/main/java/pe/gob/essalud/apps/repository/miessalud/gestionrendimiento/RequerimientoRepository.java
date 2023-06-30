package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento SET id_estado_requerimiento = ? WHERE id_requerimiento=? ", nativeQuery = true)
    public int aprobarRequerimiento(@Param("estado") Number estado, @Param("idRequerimiento") Number idRequerimiento);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento SET id_estado_requerimiento = ? , motivo= ? WHERE id_requerimiento=? ", nativeQuery = true)
    public int rechazarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("idRequerimiento") Number idRequerimiento);

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento SET id_estado_requerimiento = ? , motivo= ? , id_area_receptor= ? WHERE id_requerimiento=? ", nativeQuery = true)
    public int derivarRequerimiento(@Param("estado") Number estado, @Param("motivo") String motivo, @Param("idAreaReceptor") Number idAreaReceptor, @Param("idRequerimiento") Number idRequerimiento);

}
