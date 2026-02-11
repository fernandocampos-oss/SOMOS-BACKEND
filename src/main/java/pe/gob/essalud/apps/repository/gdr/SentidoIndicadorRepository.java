package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.gob.essalud.apps.model.gdr.SentidoIndicador;

import java.util.List;
import java.util.Optional;

@Repository
public interface SentidoIndicadorRepository extends JpaRepository<SentidoIndicador, Long> {
    
    // Buscar sentido por ID de indicador
    Optional<SentidoIndicador> findByIdIndicador(Long idIndicador);
    
    // Buscar todos los sentidos para múltiples indicadores
    List<SentidoIndicador> findByIdIndicadorIn(List<Long> idIndicadores);
    
    // Verificar si existe sentido para un indicador
    boolean existsByIdIndicador(Long idIndicador);
    
    // Eliminar por ID de indicador
    void deleteByIdIndicador(Long idIndicador);
}
