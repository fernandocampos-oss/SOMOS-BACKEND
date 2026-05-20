package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ReunionEstablecimientoMetas;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional("gdrTransactionManager")
public interface ReunionEstablecimientoMetasRepository extends JpaRepository<ReunionEstablecimientoMetas, Long> {

    /**
     * Buscar reunión por evaluado, evaluador y periodo.
     * Usa findFirst + OrderByFechaModificacionDesc para evitar NonUniqueResultException
     * en caso de registros duplicados, devolviendo siempre el más recientemente modificado.
     */
    Optional<ReunionEstablecimientoMetas> findFirstByIdVotanteEvaluadoAndIdVotanteEvaluadorAndPeriodoOrderByFechaModificacionDesc(
            Long idVotanteEvaluado, Long idVotanteEvaluador, String periodo);

    /**
     * Buscar todas las reuniones de un evaluador en un periodo
     */
    List<ReunionEstablecimientoMetas> findByIdVotanteEvaluadorAndPeriodo(
            Long idVotanteEvaluador, String periodo);

    /**
     * Buscar todas las reuniones de un evaluado en un periodo, ordenadas por id_reunion ASC
     * (el primero es el evaluador más antiguo = evaluador principal para procesos)
     */
    @Query("SELECT r FROM ReunionEstablecimientoMetas r " +
           "WHERE r.idVotanteEvaluado = :idEvaluado AND r.periodo = :periodo " +
           "ORDER BY r.idReunion ASC")
    List<ReunionEstablecimientoMetas> findByIdVotanteEvaluadoAndPeriodo(
            @Param("idEvaluado") Long idVotanteEvaluado, @Param("periodo") String periodo);

    /**
     * Buscar la primera reunión confirmada de un evaluado en un periodo (cualquier evaluador)
     */
    @Query("SELECT r FROM ReunionEstablecimientoMetas r " +
           "WHERE r.idVotanteEvaluado = :idEvaluado AND r.periodo = :periodo AND r.confirmado = true " +
           "ORDER BY r.idReunion ASC")
    Optional<ReunionEstablecimientoMetas> findFirstConfirmadaByEvaluadoAndPeriodo(
            @Param("idEvaluado") Long idVotanteEvaluado, @Param("periodo") String periodo);

    /**
     * Verificar si existe una reunión para la combinación
     */
    boolean existsByIdVotanteEvaluadoAndIdVotanteEvaluadorAndPeriodo(
            Long idVotanteEvaluado, Long idVotanteEvaluador, String periodo);

    /**
     * Verificar si ya está confirmado
     */
    @Query("SELECT r.confirmado FROM ReunionEstablecimientoMetas r " +
           "WHERE r.idVotanteEvaluado = :idEvaluado " +
           "AND r.idVotanteEvaluador = :idEvaluador " +
           "AND r.periodo = :periodo")
    Boolean estaConfirmado(@Param("idEvaluado") Long idVotanteEvaluado,
                          @Param("idEvaluador") Long idVotanteEvaluador,
                          @Param("periodo") String periodo);

    /**
     * Actualizar el estado de asistencia
     */
    @Modifying
    @Query("UPDATE ReunionEstablecimientoMetas r SET " +
           "r.asistio = :asistio, " +
           "r.fechaReunion = :fechaReunion, " +
           "r.fechaModificacion = CURRENT_TIMESTAMP " +
           "WHERE r.idReunion = :idReunion AND r.confirmado = false")
    int actualizarAsistencia(@Param("idReunion") Long idReunion,
                            @Param("asistio") String asistio,
                            @Param("fechaReunion") java.time.LocalDate fechaReunion);

    /**
     * Confirmar la reunión
     */
    @Modifying
    @Query("UPDATE ReunionEstablecimientoMetas r SET " +
           "r.confirmado = true, " +
           "r.fechaConfirmacion = CURRENT_TIMESTAMP, " +
           "r.fechaModificacion = CURRENT_TIMESTAMP " +
           "WHERE r.idReunion = :idReunion AND r.asistio <> '-' AND r.confirmado = false")
    int confirmar(@Param("idReunion") Long idReunion);

    /**
     * Reiniciar la confirmación (solo Maestro GDR puede hacer esto)
     * Resetea TODOS los registros del evaluado en ese periodo (multi-evaluador)
     */
    @Modifying
    @Query("UPDATE ReunionEstablecimientoMetas r SET " +
           "r.confirmado = false, " +
           "r.reiniciadoPor = :reiniciadoPor, " +
           "r.fechaReinicio = CURRENT_TIMESTAMP, " +
           "r.fechaModificacion = CURRENT_TIMESTAMP " +
           "WHERE r.idVotanteEvaluado = :idEvaluado AND r.periodo = :periodo")
    int reiniciarConfirmacionPorEvaluadoYPeriodo(@Param("idEvaluado") Long idVotanteEvaluado,
                                                  @Param("periodo") String periodo,
                                                  @Param("reiniciadoPor") String reiniciadoPor);

    /**
     * Contar reuniones confirmadas por periodo
     */
    long countByPeriodoAndConfirmadoTrue(String periodo);

    /**
     * Contar reuniones pendientes por periodo
     */
    long countByPeriodoAndConfirmadoFalse(String periodo);
}
