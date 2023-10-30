package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;

import java.time.LocalDateTime;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE requerimiento SET nombre=?, descripcion=?, id_tipo_ingreso=?, fecha_modificacion=?, usuario_modificacion=? WHERE id_requerimiento=? ", nativeQuery = true)
    public void modificarRequerimiento(@Param("nombre") String estado,
                                       @Param("descripcion") String descripcion,
                                       @Param("idTipoIngreso") Number idTipoIngreso,
//                                       @Param("Identificador") String Identificador,
                                       @Param("fechaModificacion") LocalDateTime fechaModificacion,
                                       @Param("usuarioModificacion") Number usuarioModificacion,
                                       @Param("idRequerimiento") Number idRequerimiento);

}
