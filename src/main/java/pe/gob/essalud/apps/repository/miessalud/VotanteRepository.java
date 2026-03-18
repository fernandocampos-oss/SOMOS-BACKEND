package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.model.miessalud.Votante;

import java.util.List;
import java.util.Optional;

public interface VotanteRepository extends JpaRepository<Votante, Integer> {

    Optional<Votante> findByNumeroDocumento(String numeroDocumento);
    List<Votante> findByIdUsuario(Integer idUsuario);
    List<Votante> findByIdSegmento(Integer idSegmento);
    
    // Paginado para evaluadores
    Page<Votante> findByIdSegmento(Integer idSegmento, Pageable pageable);
    
    // Buscar por segmento y filtro de DNI
    Page<Votante> findByIdSegmentoAndNumeroDocumentoContaining(Integer idSegmento, String numeroDocumento, Pageable pageable);
    
    // Buscar evaluador exacto por DNI
    Optional<Votante> findByIdSegmentoAndNumeroDocumento(Integer idSegmento, String numeroDocumento);
    
    // Contar evaluadores
    long countByIdSegmento(Integer idSegmento);
    
    // Buscar evaluadores que están en la lista de IDs (para filtro "con trabajadores")
    Page<Votante> findByIdSegmentoAndIdVotanteIn(Integer idSegmento, List<Integer> idVotantes, Pageable pageable);
    
    // Buscar evaluadores que están en la lista de IDs y filtrar por DNI
    Page<Votante> findByIdSegmentoAndIdVotanteInAndNumeroDocumentoContaining(Integer idSegmento, List<Integer> idVotantes, String numeroDocumento, Pageable pageable);

}
