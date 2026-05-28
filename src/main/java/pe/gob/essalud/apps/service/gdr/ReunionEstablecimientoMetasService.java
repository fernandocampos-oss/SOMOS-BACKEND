package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ReunionEstablecimientoMetas;
import pe.gob.essalud.apps.repository.gdr.ReunionEstablecimientoMetasRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReunionEstablecimientoMetasService {

    private final ReunionEstablecimientoMetasRepository repository;
    private final MaestroGdrService maestroGdrService;

    /**
     * Obtener o crear registro de reunión para la combinación evaluado-evaluador-periodo
     */
    @Transactional("gdrTransactionManager")
    public ReunionEstablecimientoMetas obtenerOCrear(Long idVotanteEvaluado, Long idVotanteEvaluador, String periodo) {
        log.info("Buscando reunión para evaluado={}, evaluador={}, periodo={}", 
                idVotanteEvaluado, idVotanteEvaluador, periodo);
        
        Optional<ReunionEstablecimientoMetas> existente = repository
                .findFirstByIdVotanteEvaluadoAndIdVotanteEvaluadorAndPeriodoOrderByFechaModificacionDesc(
                        idVotanteEvaluado, idVotanteEvaluador, periodo);
        
        if (existente.isPresent()) {
            ReunionEstablecimientoMetas reunion = existente.get();
            log.info("Reunión existente encontrada con ID: {}", reunion.getIdReunion());
            // Si existe pero no está confirmada, verificar si hay otra confirmada para este trabajador
            // (caso: segundo evaluador asignado antes del fix, o registro creado vacío)
            if (!Boolean.TRUE.equals(reunion.getConfirmado())) {
                Optional<ReunionEstablecimientoMetas> confirmadaExistente =
                        repository.findFirstByIdVotanteEvaluadoAndPeriodoAndConfirmadoIsTrueOrderByIdReunionAsc(idVotanteEvaluado, periodo);
                if (confirmadaExistente.isPresent()) {
                    ReunionEstablecimientoMetas fuente = confirmadaExistente.get();
                    reunion.setConfirmado(true);
                    reunion.setAsistio(fuente.getAsistio());
                    reunion.setFechaReunion(fuente.getFechaReunion());
                    reunion.setFechaConfirmacion(fuente.getFechaConfirmacion());
                    log.info("Sincronizando estado confirmado de reunión ID={} al evaluador actual", fuente.getIdReunion());
                    return repository.save(reunion);
                }
            }
            return reunion;
        }
        
        // Crear nueva: si el trabajador ya tiene una reunión confirmada por otro evaluador,
        // heredar ese estado para que el segundo evaluador vea el formato como ya registrado
        ReunionEstablecimientoMetas nueva = new ReunionEstablecimientoMetas(
                idVotanteEvaluado, idVotanteEvaluador, periodo);
        Optional<ReunionEstablecimientoMetas> confirmadaExistente =
                repository.findFirstByIdVotanteEvaluadoAndPeriodoAndConfirmadoIsTrueOrderByIdReunionAsc(idVotanteEvaluado, periodo);
        if (confirmadaExistente.isPresent()) {
            ReunionEstablecimientoMetas fuente = confirmadaExistente.get();
            nueva.setConfirmado(true);
            nueva.setAsistio(fuente.getAsistio());
            nueva.setFechaReunion(fuente.getFechaReunion());
            nueva.setFechaConfirmacion(fuente.getFechaConfirmacion());
            log.info("Heredando estado confirmado de reunión ID={} al nuevo evaluador", fuente.getIdReunion());
        }
        ReunionEstablecimientoMetas guardada = repository.save(nueva);
        log.info("Nueva reunión creada con ID: {}", guardada.getIdReunion());
        return guardada;
    }

    /**
     * Buscar reunión por ID
     */
    public Optional<ReunionEstablecimientoMetas> buscarPorId(Long idReunion) {
        return repository.findById(idReunion);
    }

    /**
     * Buscar reunión específica
     */
    public Optional<ReunionEstablecimientoMetas> buscar(Long idVotanteEvaluado, Long idVotanteEvaluador, String periodo) {
        return repository.findFirstByIdVotanteEvaluadoAndIdVotanteEvaluadorAndPeriodoOrderByFechaModificacionDesc(
                idVotanteEvaluado, idVotanteEvaluador, periodo);
    }

    /**
     * Listar reuniones de un evaluador en un periodo
     */
    public List<ReunionEstablecimientoMetas> listarPorEvaluador(Long idVotanteEvaluador, String periodo) {
        return repository.findByIdVotanteEvaluadorAndPeriodo(idVotanteEvaluador, periodo);
    }

    /**
     * Listar reuniones de un evaluado en un periodo (para Maestro GDR)
     */
    public List<ReunionEstablecimientoMetas> listarPorEvaluado(Long idVotanteEvaluado, String periodo) {
        return repository.findByIdVotanteEvaluadoAndPeriodo(idVotanteEvaluado, periodo);
    }

    /**
     * Actualizar asistencia (solo si no está confirmado)
     */
    @Transactional("gdrTransactionManager")
    public ReunionEstablecimientoMetas actualizarAsistencia(Long idReunion, String asistio, LocalDate fechaReunion) {
        log.info("Actualizando asistencia reunión={}, asistio={}, fecha={}", idReunion, asistio, fechaReunion);
        
        ReunionEstablecimientoMetas reunion = repository.findById(idReunion)
                .orElseThrow(() -> new RuntimeException("Reunión no encontrada: " + idReunion));
        
        if (reunion.getConfirmado()) {
            // Ya está confirmada (p.ej. heredada del coordinador): no-op, devolver tal cual
            return reunion;
        }
        
        // Validar valor de asistio
        if (!"-".equals(asistio) && !"S".equals(asistio) && !"N".equals(asistio)) {
            throw new RuntimeException("Valor de asistencia inválido. Use '-', 'S' o 'N'");
        }
        
        // Si es "No" o "-", la fecha debe ser null
        if (!"S".equals(asistio)) {
            fechaReunion = null;
        }
        // Nota: La fecha solo es obligatoria al CONFIRMAR, no al cambiar asistencia
        
        reunion.setAsistio(asistio);
        reunion.setFechaReunion(fechaReunion);
        
        return repository.save(reunion);
    }

    /**
     * Confirmar la reunión
     */
    @Transactional("gdrTransactionManager")
    public ReunionEstablecimientoMetas confirmar(Long idReunion) {
        log.info("Confirmando reunión: {}", idReunion);
        
        ReunionEstablecimientoMetas reunion = repository.findById(idReunion)
                .orElseThrow(() -> new RuntimeException("Reunión no encontrada: " + idReunion));
        
        if (reunion.getConfirmado()) {
            throw new RuntimeException("La reunión ya está confirmada");
        }
        
        if ("-".equals(reunion.getAsistio())) {
            throw new RuntimeException("Debe seleccionar si asistió o no antes de confirmar");
        }
        
        reunion.setConfirmado(true);
        reunion.setFechaConfirmacion(LocalDateTime.now());
        
        ReunionEstablecimientoMetas guardada = repository.save(reunion);
        log.info("Reunión confirmada exitosamente: {}", idReunion);

        // Propagar confirmación a otros evaluadores del mismo evaluado+periodo
        // (un evaluado puede tener más de un evaluador asignado)
        List<ReunionEstablecimientoMetas> hermanas = repository
                .findByIdVotanteEvaluadoAndPeriodo(guardada.getIdVotanteEvaluado(), guardada.getPeriodo());
        for (ReunionEstablecimientoMetas hermana : hermanas) {
            if (!hermana.getIdReunion().equals(guardada.getIdReunion()) && !Boolean.TRUE.equals(hermana.getConfirmado())) {
                hermana.setConfirmado(true);
                hermana.setAsistio(guardada.getAsistio());
                hermana.setFechaReunion(guardada.getFechaReunion());
                hermana.setFechaConfirmacion(guardada.getFechaConfirmacion());
                repository.save(hermana);
                log.info("Confirmación propagada a reunión hermana ID={}", hermana.getIdReunion());
            }
        }

        return guardada;
    }

    /**
     * Reiniciar confirmación (solo Maestro GDR)
     */
    @Transactional("gdrTransactionManager")
    public ReunionEstablecimientoMetas reiniciarConfirmacion(Long idReunion, String dniMaestroGdr) {
        log.info("Reiniciando confirmación reunión={} por maestro={}", idReunion, dniMaestroGdr);
        
        if (!maestroGdrService.esMaestroGdrPorDni(dniMaestroGdr)) {
            throw new RuntimeException("Solo un Maestro GDR puede reiniciar confirmaciones");
        }
        
        ReunionEstablecimientoMetas reunion = repository.findById(idReunion)
                .orElseThrow(() -> new RuntimeException("Reunión no encontrada: " + idReunion));
        
        if (!reunion.getConfirmado()) {
            throw new RuntimeException("La reunión no está confirmada");
        }
        
        // Resetear TODOS los registros del evaluado en ese periodo (cubre multi-evaluador)
        Long idEvaluado = reunion.getIdVotanteEvaluado();
        String periodo = reunion.getPeriodo();
        repository.reiniciarConfirmacionPorEvaluadoYPeriodo(idEvaluado, periodo, dniMaestroGdr);
        
        ReunionEstablecimientoMetas guardada = repository.findById(idReunion)
                .orElseThrow(() -> new RuntimeException("Reunión no encontrada: " + idReunion));
        log.info("Confirmación reiniciada para evaluado={}, periodo={}", idEvaluado, periodo);
        return guardada;
    }

    /**
     * Verificar si ya está confirmado
     */
    public boolean estaConfirmado(Long idVotanteEvaluado, Long idVotanteEvaluador, String periodo) {
        return repository.findFirstByIdVotanteEvaluadoAndIdVotanteEvaluadorAndPeriodoOrderByFechaModificacionDesc(
                idVotanteEvaluado, idVotanteEvaluador, periodo)
                .map(ReunionEstablecimientoMetas::getConfirmado)
                .orElse(false);
    }

    /**
     * Estadísticas por periodo
     */
    public long contarConfirmados(String periodo) {
        return repository.countByPeriodoAndConfirmadoTrue(periodo);
    }

    public long contarPendientes(String periodo) {
        return repository.countByPeriodoAndConfirmadoFalse(periodo);
    }
}
