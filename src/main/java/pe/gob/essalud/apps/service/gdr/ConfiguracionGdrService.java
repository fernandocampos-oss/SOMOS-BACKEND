package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ConfiguracionGdr;
import pe.gob.essalud.apps.repository.gdr.ConfiguracionGdrRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para gestionar la configuración de fases y evidencias del ciclo GDR.
 * Esta configuración es GLOBAL por periodo y aplica a todos los evaluadores.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracionGdrService {

    private final ConfiguracionGdrRepository repository;

    /**
     * Obtener o crear configuración para un periodo.
     * Si no existe, crea una con valores por defecto.
     */
    @Transactional("gdrTransactionManager")
    public ConfiguracionGdr obtenerOCrear(String periodo) {
        log.info("Buscando configuración para periodo={}", periodo);
        
        Optional<ConfiguracionGdr> existente = repository.findByPeriodo(periodo);
        
        if (existente.isPresent()) {
            log.info("Configuración existente encontrada para periodo: {}", periodo);
            return existente.get();
        }
        
        // Crear nueva con valores por defecto
        ConfiguracionGdr nueva = new ConfiguracionGdr(periodo);
        ConfiguracionGdr guardada = repository.save(nueva);
        log.info("Nueva configuración creada para periodo: {} con ID: {}", periodo, guardada.getIdConfiguracion());
        return guardada;
    }

    /**
     * Obtener configuración por periodo (sin crear si no existe)
     */
    public Optional<ConfiguracionGdr> buscarPorPeriodo(String periodo) {
        return repository.findByPeriodo(periodo);
    }

    /**
     * Actualizar configuración de fases
     */
    @Transactional("gdrTransactionManager")
    public ConfiguracionGdr actualizarFases(String periodo, Boolean fasePreActiva, Boolean fasePlanificacionActiva,
                                            Boolean faseSeguimientoActiva, Boolean faseEvaluacionActiva,
                                            Boolean fasePostActiva, String modificadoPor) {
        log.info("Actualizando fases para periodo={}", periodo);
        
        ConfiguracionGdr config = obtenerOCrear(periodo);
        
        if (fasePreActiva != null) config.setFasePreActiva(fasePreActiva);
        if (fasePlanificacionActiva != null) config.setFasePlanificacionActiva(fasePlanificacionActiva);
        if (faseSeguimientoActiva != null) config.setFaseSeguimientoActiva(faseSeguimientoActiva);
        if (faseEvaluacionActiva != null) config.setFaseEvaluacionActiva(faseEvaluacionActiva);
        if (fasePostActiva != null) config.setFasePostActiva(fasePostActiva);
        
        config.setModificadoPor(modificadoPor);
        config.setFechaModificacion(LocalDateTime.now());
        
        return repository.save(config);
    }

    /**
     * Actualizar configuración de evidencias
     */
    @Transactional("gdrTransactionManager")
    public ConfiguracionGdr actualizarEvidencias(String periodo, Boolean evidencia1Activa,
                                                  Boolean evidencia2Activa, Boolean evidenciaFinalActiva,
                                                  String modificadoPor) {
        log.info("Actualizando evidencias para periodo={}", periodo);
        
        ConfiguracionGdr config = obtenerOCrear(periodo);
        
        if (evidencia1Activa != null) config.setEvidencia1Activa(evidencia1Activa);
        if (evidencia2Activa != null) config.setEvidencia2Activa(evidencia2Activa);
        if (evidenciaFinalActiva != null) config.setEvidenciaFinalActiva(evidenciaFinalActiva);
        
        config.setModificadoPor(modificadoPor);
        config.setFechaModificacion(LocalDateTime.now());
        
        return repository.save(config);
    }

    /**
     * Actualizar toda la configuración de una vez
     */
    @Transactional("gdrTransactionManager")
    public ConfiguracionGdr actualizarConfiguracion(ConfiguracionGdr configActualizada, String modificadoPor) {
        log.info("Actualizando configuración completa para periodo={}", configActualizada.getPeriodo());
        
        ConfiguracionGdr config = obtenerOCrear(configActualizada.getPeriodo());
        
        // Actualizar fases
        config.setFasePreActiva(configActualizada.getFasePreActiva());
        config.setFasePlanificacionActiva(configActualizada.getFasePlanificacionActiva());
        config.setFaseSeguimientoActiva(configActualizada.getFaseSeguimientoActiva());
        config.setFaseEvaluacionActiva(configActualizada.getFaseEvaluacionActiva());
        config.setFasePostActiva(configActualizada.getFasePostActiva());
        
        // Actualizar evidencias
        config.setEvidencia1Activa(configActualizada.getEvidencia1Activa());
        config.setEvidencia2Activa(configActualizada.getEvidencia2Activa());
        config.setEvidenciaFinalActiva(configActualizada.getEvidenciaFinalActiva());
        
        // Auditoría
        config.setModificadoPor(modificadoPor);
        config.setFechaModificacion(LocalDateTime.now());
        
        return repository.save(config);
    }

    /**
     * Verificar si una fase está activa
     */
    public boolean esFaseActiva(String periodo, String fase) {
        Optional<ConfiguracionGdr> config = repository.findByPeriodo(periodo);
        if (config.isEmpty()) {
            return false;
        }
        
        ConfiguracionGdr c = config.get();
        switch (fase.toUpperCase()) {
            case "PRE": return Boolean.TRUE.equals(c.getFasePreActiva());
            case "PLANIFICACION": return Boolean.TRUE.equals(c.getFasePlanificacionActiva());
            case "SEGUIMIENTO": return Boolean.TRUE.equals(c.getFaseSeguimientoActiva());
            case "EVALUACION": return Boolean.TRUE.equals(c.getFaseEvaluacionActiva());
            case "POST": return Boolean.TRUE.equals(c.getFasePostActiva());
            default: return false;
        }
    }

    /**
     * Verificar si una evidencia está activa
     */
    public boolean esEvidenciaActiva(String periodo, int numeroEvidencia) {
        Optional<ConfiguracionGdr> config = repository.findByPeriodo(periodo);
        if (config.isEmpty()) {
            return false;
        }
        
        ConfiguracionGdr c = config.get();
        switch (numeroEvidencia) {
            case 1: return Boolean.TRUE.equals(c.getEvidencia1Activa());
            case 2: return Boolean.TRUE.equals(c.getEvidencia2Activa());
            case 3: return Boolean.TRUE.equals(c.getEvidenciaFinalActiva()); // 3 = final
            default: return false;
        }
    }
}
