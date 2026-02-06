package pe.gob.essalud.apps.service.gdr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.SentidoIndicador;
import pe.gob.essalud.apps.repository.gdr.SentidoIndicadorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SentidoIndicadorService {

    @Autowired
    private SentidoIndicadorRepository sentidoIndicadorRepository;

    // Obtener sentido por ID de indicador
    public Optional<SentidoIndicador> obtenerPorIdIndicador(Long idIndicador) {
        return sentidoIndicadorRepository.findByIdIndicador(idIndicador);
    }

    // Obtener sentidos para múltiples indicadores (retorna Map para fácil acceso)
    public Map<Long, String> obtenerSentidosPorIndicadores(List<Long> idIndicadores) {
        log.info("=== obtenerSentidosPorIndicadores: buscando {} IDs ===", idIndicadores.size());
        log.info("IDs solicitados: {}", idIndicadores);
        
        List<SentidoIndicador> sentidos = sentidoIndicadorRepository.findByIdIndicadorIn(idIndicadores);
        log.info("Registros encontrados en BD: {}", sentidos.size());
        
        for (SentidoIndicador s : sentidos) {
            log.info("  - ID: {}, idIndicador: {}, sentido: {}", s.getId(), s.getIdIndicador(), s.getSentido());
        }
        
        Map<Long, String> resultado = sentidos.stream()
                .collect(Collectors.toMap(
                        SentidoIndicador::getIdIndicador,
                        SentidoIndicador::getSentido
                ));
        log.info("Map a retornar: {}", resultado);
        return resultado;
    }

    // Guardar o actualizar sentido - usa REQUIRES_NEW para transacción independiente
    @Transactional(value = "gdrTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public SentidoIndicador guardarOActualizar(Long idIndicador, String sentido) {
        log.info("=== guardarOActualizar INICIO: idIndicador={}, sentido={} ===", idIndicador, sentido);
        try {
            Optional<SentidoIndicador> existente = sentidoIndicadorRepository.findByIdIndicador(idIndicador);
            log.info("Registro existente: {}", existente.isPresent());
            
            SentidoIndicador resultado;
            if (existente.isPresent()) {
                // Actualizar
                SentidoIndicador sentidoIndicador = existente.get();
                sentidoIndicador.setSentido(sentido);
                resultado = sentidoIndicadorRepository.save(sentidoIndicador);
                log.info("Registro ACTUALIZADO: id={}", resultado.getId());
            } else {
                // Crear nuevo
                SentidoIndicador nuevo = new SentidoIndicador(idIndicador, sentido);
                resultado = sentidoIndicadorRepository.save(nuevo);
                log.info("Registro CREADO: id={}", resultado.getId());
            }
            
            // Verificar que se guardó
            Optional<SentidoIndicador> verificacion = sentidoIndicadorRepository.findByIdIndicador(idIndicador);
            log.info("Verificación post-guardado: encontrado={}, sentido={}", 
                verificacion.isPresent(), 
                verificacion.map(SentidoIndicador::getSentido).orElse("N/A"));
            
            log.info("=== guardarOActualizar FIN EXITOSO ===");
            return resultado;
        } catch (Exception e) {
            log.error("=== guardarOActualizar ERROR: {} - {} ===", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }

    // Eliminar sentido
    @Transactional(value = "gdrTransactionManager")
    public void eliminarPorIdIndicador(Long idIndicador) {
        sentidoIndicadorRepository.deleteByIdIndicador(idIndicador);
    }

    // Obtener todos
    public List<SentidoIndicador> obtenerTodos() {
        return sentidoIndicadorRepository.findAll();
    }
}
