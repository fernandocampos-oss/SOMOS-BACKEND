package pe.gob.essalud.apps.service.gdr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.SentidoIndicador;
import pe.gob.essalud.apps.repository.gdr.SentidoIndicadorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<SentidoIndicador> sentidos = sentidoIndicadorRepository.findByIdIndicadorIn(idIndicadores);
        return sentidos.stream()
                .collect(Collectors.toMap(
                        SentidoIndicador::getIdIndicador,
                        SentidoIndicador::getSentido
                ));
    }

    // Guardar o actualizar sentido
    @Transactional
    public SentidoIndicador guardarOActualizar(Long idIndicador, String sentido) {
        Optional<SentidoIndicador> existente = sentidoIndicadorRepository.findByIdIndicador(idIndicador);
        
        if (existente.isPresent()) {
            // Actualizar
            SentidoIndicador sentidoIndicador = existente.get();
            sentidoIndicador.setSentido(sentido);
            return sentidoIndicadorRepository.save(sentidoIndicador);
        } else {
            // Crear nuevo
            SentidoIndicador nuevo = new SentidoIndicador(idIndicador, sentido);
            return sentidoIndicadorRepository.save(nuevo);
        }
    }

    // Eliminar sentido
    @Transactional
    public void eliminarPorIdIndicador(Long idIndicador) {
        sentidoIndicadorRepository.deleteByIdIndicador(idIndicador);
    }

    // Obtener todos
    public List<SentidoIndicador> obtenerTodos() {
        return sentidoIndicadorRepository.findAll();
    }
}
