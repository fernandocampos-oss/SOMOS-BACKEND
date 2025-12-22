package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ValorAlcanzadoPrioridad;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional("gdrTransactionManager")
public interface ValorAlcanzadoPrioridadRepository extends JpaRepository<ValorAlcanzadoPrioridad, Long> {

    Optional<ValorAlcanzadoPrioridad> findByIdPrioridad(Long idPrioridad);

    List<ValorAlcanzadoPrioridad> findByIdPrioridadIn(List<Long> idsPrioridad);

    void deleteByIdPrioridad(Long idPrioridad);
}
