package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ConfiguracionGdr;

import java.util.Optional;

@Repository
@Transactional("gdrTransactionManager")
public interface ConfiguracionGdrRepository extends JpaRepository<ConfiguracionGdr, Long> {

    /**
     * Buscar configuración por periodo
     */
    Optional<ConfiguracionGdr> findByPeriodo(String periodo);

    /**
     * Verificar si existe configuración para un periodo
     */
    boolean existsByPeriodo(String periodo);
}
