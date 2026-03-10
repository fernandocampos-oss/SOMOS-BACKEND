package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.SegmentoGdr;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional("gdrTransactionManager")
public interface SegmentoGdrRepository extends JpaRepository<SegmentoGdr, Long> {

    Optional<SegmentoGdr> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);

    List<SegmentoGdr> findAllByOrderByFechaCreacionDesc();

    List<SegmentoGdr> findByNumeroDocumentoContaining(String numeroDocumento);

    @Modifying
    @Query("UPDATE SegmentoGdr s SET s.segmento = :segmento, s.fechaModificacion = CURRENT_TIMESTAMP WHERE s.numeroDocumento = :dni")
    int actualizarSegmentoPorDni(@Param("dni") String dni, @Param("segmento") String segmento);

    @Modifying
    @Query("DELETE FROM SegmentoGdr s WHERE s.numeroDocumento = :dni")
    int eliminarPorDni(@Param("dni") String dni);

    long count();
}
