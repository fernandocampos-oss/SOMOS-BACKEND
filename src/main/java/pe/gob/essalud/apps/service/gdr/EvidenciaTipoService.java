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
        Optional<EvidenciaTipo> existente = evidenciaTipoRepository.findByIdEvidencia(idEvidencia);

        if (existente.isPresent()) {
            // Actualizar
            EvidenciaTipo evidenciaTipo = existente.get();
            evidenciaTipo.setTipo(tipo);
            evidenciaTipo.setOrden(orden);
            evidenciaTipo.setIdIndicador(idIndicador);
            return evidenciaTipoRepository.save(evidenciaTipo);
        } else {
            // Crear nuevo
            EvidenciaTipo nuevo = new EvidenciaTipo(idEvidencia, idIndicador, tipo, orden);
            return evidenciaTipoRepository.save(nuevo);
        }
    }

    // Obtener tipo de evidencia por ID de evidencia
    public Optional<EvidenciaTipo> obtenerPorIdEvidencia(Long idEvidencia) {
        return evidenciaTipoRepository.findByIdEvidencia(idEvidencia);
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

    // Actualizar solo la fecha de plazo
    @Transactional("gdrTransactionManager")
    public EvidenciaTipo actualizarFechaPlazo(Long idEvidencia, LocalDate fechaPlazo) {
        Optional<EvidenciaTipo> existente = evidenciaTipoRepository.findByIdEvidencia(idEvidencia);
        
        if (existente.isPresent()) {
            EvidenciaTipo evidenciaTipo = existente.get();
            evidenciaTipo.setFechaPlazo(fechaPlazo);
            return evidenciaTipoRepository.save(evidenciaTipo);
        } else {
            throw new RuntimeException("No se encontró evidencia con ID: " + idEvidencia);
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
