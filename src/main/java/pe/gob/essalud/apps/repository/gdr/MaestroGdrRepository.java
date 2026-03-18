package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.MaestroGdr;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional("gdrTransactionManager")
public interface MaestroGdrRepository extends JpaRepository<MaestroGdr, Long> {

    Optional<MaestroGdr> findByNumeroDocumento(String numeroDocumento);

    Optional<MaestroGdr> findByNumeroDocumentoAndEstadoTrue(String numeroDocumento);

    boolean existsByNumeroDocumentoAndEstadoTrue(String numeroDocumento);

    List<MaestroGdr> findByEstadoTrueOrderByFechaCreacionDesc();

    List<MaestroGdr> findAllByOrderByFechaCreacionDesc();

    @Modifying
    @Query("UPDATE MaestroGdr m SET m.estado = false, m.fechaModificacion = CURRENT_TIMESTAMP WHERE m.numeroDocumento = :dni")
    int desactivarPorDni(@Param("dni") String dni);

    @Modifying
    @Query("UPDATE MaestroGdr m SET m.estado = true, m.fechaModificacion = CURRENT_TIMESTAMP WHERE m.numeroDocumento = :dni")
    int activarPorDni(@Param("dni") String dni);

    long countByEstadoTrue();
}
