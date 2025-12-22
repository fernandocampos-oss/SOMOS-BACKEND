package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ValorAlcanzadoPrioridad;
import pe.gob.essalud.apps.repository.gdr.ValorAlcanzadoPrioridadRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValorAlcanzadoPrioridadService {

    private final ValorAlcanzadoPrioridadRepository valorAlcanzadoPrioridadRepository;

    // Guardar o actualizar valor alcanzado
    @Transactional("gdrTransactionManager")
    public ValorAlcanzadoPrioridad guardarOActualizar(Long idPrioridad, BigDecimal valorAlcanzado) {
        Optional<ValorAlcanzadoPrioridad> existente = valorAlcanzadoPrioridadRepository.findByIdPrioridad(idPrioridad);

        if (existente.isPresent()) {
            // Actualizar
            ValorAlcanzadoPrioridad valor = existente.get();
            valor.setValorAlcanzado(valorAlcanzado);
            return valorAlcanzadoPrioridadRepository.save(valor);
        } else {
            // Crear nuevo
            ValorAlcanzadoPrioridad nuevo = new ValorAlcanzadoPrioridad(idPrioridad, valorAlcanzado);
            return valorAlcanzadoPrioridadRepository.save(nuevo);
        }
    }

    // Obtener valor alcanzado por ID de prioridad
    public Optional<ValorAlcanzadoPrioridad> obtenerPorIdPrioridad(Long idPrioridad) {
        return valorAlcanzadoPrioridadRepository.findByIdPrioridad(idPrioridad);
    }

    // Obtener múltiples valores alcanzados
    public Map<Long, BigDecimal> obtenerMultiples(List<Long> idsPrioridad) {
        List<ValorAlcanzadoPrioridad> valores = valorAlcanzadoPrioridadRepository.findByIdPrioridadIn(idsPrioridad);
        
        Map<Long, BigDecimal> resultado = new HashMap<>();
        for (ValorAlcanzadoPrioridad valor : valores) {
            resultado.put(valor.getIdPrioridad(), valor.getValorAlcanzado());
        }
        
        return resultado;
    }

    // Eliminar valor alcanzado
    @Transactional("gdrTransactionManager")
    public void eliminarPorIdPrioridad(Long idPrioridad) {
        valorAlcanzadoPrioridadRepository.deleteByIdPrioridad(idPrioridad);
    }
}
