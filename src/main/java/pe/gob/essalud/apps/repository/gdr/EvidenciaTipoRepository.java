package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.essalud.apps.model.gdr.EvidenciaTipo;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenciaTipoRepository extends JpaRepository<EvidenciaTipo, Long> {

    Optional<EvidenciaTipo> findByIdEvidencia(Long idEvidencia);

    List<EvidenciaTipo> findByIdIndicadorOrderByOrden(Long idIndicador);

    @Query("SELECT et FROM EvidenciaTipo et WHERE et.idEvidencia IN :idsEvidencia")
    List<EvidenciaTipo> findByIdEvidenciaIn(@Param("idsEvidencia") List<Long> idsEvidencia);

    @Modifying
    @Query("DELETE FROM EvidenciaTipo et WHERE et.idEvidencia = :idEvidencia")
    void deleteByIdEvidencia(@Param("idEvidencia") Long idEvidencia);

    @Modifying
    @Query("DELETE FROM EvidenciaTipo et WHERE et.idIndicador = :idIndicador")
    void deleteByIdIndicador(@Param("idIndicador") Long idIndicador);
}
