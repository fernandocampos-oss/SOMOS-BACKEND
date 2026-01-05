package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ResultadosFinales;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional("gdrTransactionManager")
public interface ResultadosFinalesRepository extends JpaRepository<ResultadosFinales, Long> {
    
    Optional<ResultadosFinales> findByIdVotanteAndAnio(Long idVotante, Integer anio);
    
    List<ResultadosFinales> findByIdVotanteInAndAnio(List<Long> idsVotantes, Integer anio);
    
    void deleteByIdVotanteAndAnio(Long idVotante, Integer anio);
}
