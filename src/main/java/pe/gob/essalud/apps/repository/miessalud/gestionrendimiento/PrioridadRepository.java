package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvaluadorResponseDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Prioridad;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PrioridadRepository extends JpaRepository<Prioridad, Integer> {

    @Query("SELECT i from Indicador i WHERE i.esAsignado=false ")
    List<Indicador> getAllIndicadorOrganizar();

    @Transactional
    @Modifying
    @Query(value = "UPDATE indicador SET id_prioridad=:idPrioridad, es_asignado=TRUE WHERE id_indicador IN :listIdIndicadores ", nativeQuery = true)
    public void actualizarPrioridadEnListaIndicadores(@Param("idPrioridad") Number idPrioridad, @Param("listIdIndicadores") int[] listIdIndicadores);

    @Query(value = "SELECT * from prioridad p WHERE p.id_prioridad IN (SELECT DISTINCT id_prioridad from indicador i WHERE i.id_usuario=?) ", nativeQuery = true)
    List<Prioridad> getListIdPrioridadesByTrabajador(@Param("idTrabajador") Number idTrabajador);

    @Query(value = "SELECT u.id_usuario as idUsuario, u.nombres as nombres, u.apellidos as apellidos, u.cargo as puesto, u.cod_unidad as unidad, u.numero_documento as numeroDocumento FROM usuario u WHERE u.id_usuario=:idUsuario ", nativeQuery = true)
    EvaluadorResponseDto findUsuarioById(@Param("idUsuario") Number idUsuario);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE indicador_usuario SET id_estado_requerimiento =6, fecha_finalizacion=? WHERE id_indicador_usuario =? ", nativeQuery = true)
//    public int finalizarTareaAdministrador(@Param("fechaFinalizacion") LocalDateTime fechaFinalizacion, @Param("idRequerimientoUsuario") Number idRequerimientoUsuario);

}
