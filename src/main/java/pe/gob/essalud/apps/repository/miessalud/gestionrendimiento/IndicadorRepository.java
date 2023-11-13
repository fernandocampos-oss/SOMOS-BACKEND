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

//    @Query(value = "SELECT * from indicador i WHERE i.id_usuario=? AND iu.id_estado_indicador in (1,2)  ORDER BY iu.id_indicador_usuario ASC ", nativeQuery = true)
    @Query(value = "SELECT * from indicador i WHERE i.id_usuario=? ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<Indicador> getListIndicadoresPendientesByUser(@Param("idUsuario") Number idUsuario);

    @Query(value = "SELECT * from indicador i WHERE i.id_usuario=? AND i.id_prioridad=? ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<Indicador> getListIndicadoresByUsuarioAndPrioridad(@Param("idUsuario") int idUsuario, @Param("idPrioridad") int idPrioridad);

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


    @Query(value = "SELECT * from indicador i WHERE i.id_usuario=? AND i.id_estado_indicador =1 ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<Indicador> getListIndicadoresFinalizadoByUser(@Param("idUsuario") Number idUsuario);

}
