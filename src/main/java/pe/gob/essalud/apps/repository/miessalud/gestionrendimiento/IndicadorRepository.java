package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;

import java.time.LocalDateTime;
import java.util.List;

public interface IndicadorRepository extends JpaRepository<Indicador, Integer> {

    @Query(value = "SELECT * from indicador i WHERE i.id_votante=? AND i.id_prioridad=? ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<Indicador> getListIndicadoresByUsuarioAndPrioridad(@Param("idVotante") int idVotante, @Param("idPrioridad") int idPrioridad);

    @Transactional
    @Modifying
    @Query(value = "UPDATE indicador SET descripcion=?, detalle=?, id_tipo_valor_meta=?, valor_meta=?, fecha_modificacion=?, usuario_modificacion=? WHERE id_indicador=? ", nativeQuery = true)
    public void modificarIndicador(@Param("descripcion") String descripcion,
                                   @Param("detalle") String detalle,
                                   @Param("idTipoValorMeta") Number idTipoValorMeta,
                                   @Param("valorMeta") int valorMeta,
                                   @Param("fechaModificacion") LocalDateTime fechaModificacion,
                                   @Param("usuarioModificacion") Number usuarioModificacion,
                                   @Param("idIndicador") Number idIndicador);

    @Transactional
    @Modifying
    @Query(value = "UPDATE indicador SET peso=? WHERE id_indicador=? ", nativeQuery = true)
    public int asignarPesoIndicador(@Param("peso") Number peso, @Param("idIndicador") Number idIndicador);

}
