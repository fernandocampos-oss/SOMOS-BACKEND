package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;

import java.time.LocalDateTime;

public interface IndicadorRepository extends JpaRepository<Indicador, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE indicador SET nombre=?, descripcion=?, id_tipo_ingreso=?, id_tipo_valor_meta=?, valor_meta=?, fecha_modificacion=?, usuario_modificacion=? WHERE id_indicador=? ", nativeQuery = true)
    public void modificarIndicador(@Param("nombre") String estado,
                                       @Param("descripcion") String descripcion,
                                       @Param("idTipoIngreso") Number idTipoIngreso,
                                       @Param("idTipoValorMeta") Number idTipoValorMeta,
                                       @Param("valorMeta") int valorMeta,
                                       @Param("fechaModificacion") LocalDateTime fechaModificacion,
                                       @Param("usuarioModificacion") Number usuarioModificacion,
                                       @Param("idIndicador") Number idIndicador);

}
