package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.EvidenciaTipo;
import pe.gob.essalud.apps.repository.gdr.EvidenciaTipoRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvidenciaTipoService {

    private final EvidenciaTipoRepository evidenciaTipoRepository;

    // Guardar o actualizar tipo de evidencia
    @Transactional("gdrTransactionManager")
    public EvidenciaTipo guardarOActualizar(Long idEvidencia, Long idIndicador, String tipo, Integer orden) {
        log.info("guardarOActualizar: idEvidencia={}, idIndicador={}, tipo={}, orden={}", 
            idEvidencia, idIndicador, tipo, orden);
        try {
            Optional<EvidenciaTipo> existente = evidenciaTipoRepository.findByIdEvidencia(idEvidencia);

            if (existente.isPresent()) {
                // Actualizar
                log.info("Actualizando registro existente");
                EvidenciaTipo evidenciaTipo = existente.get();
                evidenciaTipo.setTipo(tipo);
                evidenciaTipo.setOrden(orden);
                evidenciaTipo.setIdIndicador(idIndicador);
                EvidenciaTipo saved = evidenciaTipoRepository.save(evidenciaTipo);
                log.info("Registro actualizado exitosamente: id={}", saved.getId());
                return saved;
            } else {
                // Crear nuevo
                log.info("Creando nuevo registro");
                EvidenciaTipo nuevo = new EvidenciaTipo(idEvidencia, idIndicador, tipo, orden);
                EvidenciaTipo saved = evidenciaTipoRepository.save(nuevo);
                log.info("Registro creado exitosamente: id={}", saved.getId());
                return saved;
            }
        } catch (Exception e) {
            log.error("ERROR en guardarOActualizar: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    // Guardar o actualizar tipo de evidencia CON fecha de plazo
    @Transactional("gdrTransactionManager")
    public EvidenciaTipo guardarOActualizar(Long idEvidencia, Long idIndicador, String tipo, Integer orden, LocalDate fechaPlazo) {
        Optional<EvidenciaTipo> existente = evidenciaTipoRepository.findByIdEvidencia(idEvidencia);

        if (existente.isPresent()) {
            // Actualizar
            EvidenciaTipo evidenciaTipo = existente.get();
            evidenciaTipo.setTipo(tipo);
            evidenciaTipo.setOrden(orden);
            evidenciaTipo.setIdIndicador(idIndicador);
            evidenciaTipo.setFechaPlazo(fechaPlazo);
            return evidenciaTipoRepository.save(evidenciaTipo);
        } else {
            // Crear nuevo
            EvidenciaTipo nuevo = new EvidenciaTipo(idEvidencia, idIndicador, tipo, orden);
            nuevo.setFechaPlazo(fechaPlazo);
            return evidenciaTipoRepository.save(nuevo);
        }
    }

    // Obtener tipo de evidencia por ID de evidencia
    public Optional<EvidenciaTipo> obtenerPorIdEvidencia(Long idEvidencia) {
        return evidenciaTipoRepository.findByIdEvidencia(idEvidencia);
    }
    
    // Obtener todos los registros
    public List<EvidenciaTipo> obtenerTodas() {
        return evidenciaTipoRepository.findAll();
    }

    // Obtener todas las evidencias de un indicador ordenadas
    public List<EvidenciaTipo> obtenerPorIndicador(Long idIndicador) {
        return evidenciaTipoRepository.findByIdIndicadorOrderByOrden(idIndicador);
    }

    // Obtener múltiples tipos de evidencia
    public Map<Long, Map<String, Object>> obtenerMultiples(List<Long> idsEvidencia) {
        List<EvidenciaTipo> tipos = evidenciaTipoRepository.findByIdEvidenciaIn(idsEvidencia);
        
        Map<Long, Map<String, Object>> resultado = new HashMap<>();
        for (EvidenciaTipo tipo : tipos) {
            Map<String, Object> info = new HashMap<>();
            info.put("tipo", tipo.getTipo());
            info.put("orden", tipo.getOrden());
            info.put("idIndicador", tipo.getIdIndicador());
            info.put("fechaPlazo", tipo.getFechaPlazo());
            resultado.put(tipo.getIdEvidencia(), info);
        }
        
        return resultado;
    }
    
    // Obtener fechas de plazo final por lista de indicadores
    public Map<Long, LocalDate> obtenerFechasPlazoFinalPorIndicadores(List<Long> idsIndicador) {
        log.info("=== obtenerFechasPlazoFinalPorIndicadores: {} IDs ===", idsIndicador.size());
        log.info("IDs solicitados: {}", idsIndicador);
        
        Map<Long, LocalDate> resultado = new HashMap<>();
        
        for (Long idIndicador : idsIndicador) {
            Optional<EvidenciaTipo> evidenciaFinal = evidenciaTipoRepository.findFirstByIdIndicadorAndTipo(idIndicador, "final");
            log.info("Buscando idIndicador={}, encontrado={}", idIndicador, evidenciaFinal.isPresent());
            if (evidenciaFinal.isPresent()) {
                log.info("  -> fechaPlazo={}", evidenciaFinal.get().getFechaPlazo());
                if (evidenciaFinal.get().getFechaPlazo() != null) {
                    resultado.put(idIndicador, evidenciaFinal.get().getFechaPlazo());
                }
            }
        }
        
        log.info("Resultado final: {}", resultado);
        return resultado;
    }
    
    // Guardar fecha de plazo final para un indicador (sin necesidad de evidencia existente)
    @Transactional("gdrTransactionManager")
    public EvidenciaTipo guardarFechaPlazoFinalPorIndicador(Long idIndicador, LocalDate fechaPlazo) {
        log.info("guardarFechaPlazoFinalPorIndicador: idIndicador={}, fechaPlazo={}", idIndicador, fechaPlazo);
        try {
            // Buscar si ya existe un registro de evidencia final para este indicador
            Optional<EvidenciaTipo> existente = evidenciaTipoRepository.findFirstByIdIndicadorAndTipo(idIndicador, "final");
            log.info("Registro existente encontrado: {}", existente.isPresent());
            
            if (existente.isPresent()) {
                // Actualizar la fecha existente
                log.info("Actualizando fecha en registro existente");
                EvidenciaTipo evidenciaTipo = existente.get();
                evidenciaTipo.setFechaPlazo(fechaPlazo);
                EvidenciaTipo saved = evidenciaTipoRepository.save(evidenciaTipo);
                log.info("Fecha actualizada exitosamente: id={}", saved.getId());
                return saved;
            } else {
                // Crear nuevo registro con idEvidencia = -idIndicador (placeholder único por indicador)
                Long idEvidenciaPlaceholder = -idIndicador;
                log.info("Creando nuevo registro con placeholder: idEvidencia={}", idEvidenciaPlaceholder);
                EvidenciaTipo nuevo = new EvidenciaTipo(idEvidenciaPlaceholder, idIndicador, "final", 999);
                nuevo.setFechaPlazo(fechaPlazo);
                EvidenciaTipo saved = evidenciaTipoRepository.save(nuevo);
                log.info("Registro creado exitosamente: id={}", saved.getId());
                return saved;
            }
        } catch (Exception e) {
            log.error("ERROR en guardarFechaPlazoFinalPorIndicador: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }

    // Actualizar solo la fecha de plazo
    @Transactional("gdrTransactionManager")
    public EvidenciaTipo actualizarFechaPlazo(Long idEvidencia, Long idIndicador, LocalDate fechaPlazo) {
        Optional<EvidenciaTipo> existente = evidenciaTipoRepository.findByIdEvidencia(idEvidencia);
        
        if (existente.isPresent()) {
            EvidenciaTipo evidenciaTipo = existente.get();
            evidenciaTipo.setFechaPlazo(fechaPlazo);
            return evidenciaTipoRepository.save(evidenciaTipo);
        } else {
            // Si no existe, crear el registro (evidencia final con orden 999 temporal)
            log.warn("Evidencia {} no existe en BD local, creando registro con tipo 'final'", idEvidencia);
            EvidenciaTipo nuevo = new EvidenciaTipo(idEvidencia, idIndicador, "final", 999);
            nuevo.setFechaPlazo(fechaPlazo);
            return evidenciaTipoRepository.save(nuevo);
        }
    }

    // Eliminar tipo de evidencia
    @Transactional("gdrTransactionManager")
    public void eliminarPorIdEvidencia(Long idEvidencia) {
        evidenciaTipoRepository.deleteByIdEvidencia(idEvidencia);
    }

    // Eliminar todas las evidencias de un indicador
    @Transactional("gdrTransactionManager")
    public void eliminarPorIdIndicador(Long idIndicador) {
        evidenciaTipoRepository.deleteByIdIndicador(idIndicador);
    }

    // Reordenar evidencias después de eliminar una
    @Transactional("gdrTransactionManager")
    public void reordenarEvidencias(Long idIndicador) {
        List<EvidenciaTipo> evidencias = evidenciaTipoRepository.findByIdIndicadorOrderByOrden(idIndicador);
        
        int ordenActual = 1;
        for (EvidenciaTipo evidencia : evidencias) {
            if (evidencia.getOrden() != ordenActual) {
                evidencia.setOrden(ordenActual);
                evidenciaTipoRepository.save(evidencia);
            }
            ordenActual++;
        }
    }
}
