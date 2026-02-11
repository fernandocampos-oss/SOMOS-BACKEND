package pe.gob.essalud.apps.repository.gdr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.essalud.apps.model.gdr.ComentarioEstado;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioEstadoRepository extends JpaRepository<ComentarioEstado, Long> {

    Optional<ComentarioEstado> findByIdEvidencia(Long idEvidencia);

    Optional<ComentarioEstado> findByIdEvidenciaAndTipoComentario(Long idEvidencia, String tipoComentario);

    @Query("SELECT ce FROM ComentarioEstado ce WHERE ce.idEvidencia IN :idsEvidencia")
    List<ComentarioEstado> findByIdEvidenciaIn(@Param("idsEvidencia") List<Long> idsEvidencia);

    @Query("SELECT ce FROM ComentarioEstado ce WHERE ce.idEvidencia IN :idsEvidencia AND ce.tipoComentario = :tipoComentario")
    List<ComentarioEstado> findByIdEvidenciaInAndTipoComentario(@Param("idsEvidencia") List<Long> idsEvidencia, @Param("tipoComentario") String tipoComentario);

    @Modifying
    @Query("DELETE FROM ComentarioEstado ce WHERE ce.idEvidencia = :idEvidencia")
    void deleteByIdEvidencia(@Param("idEvidencia") Long idEvidencia);

    @Modifying
    @Query("DELETE FROM ComentarioEstado ce WHERE ce.idEvidencia = :idEvidencia AND ce.tipoComentario = :tipoComentario")
    void deleteByIdEvidenciaAndTipoComentario(@Param("idEvidencia") Long idEvidencia, @Param("tipoComentario") String tipoComentario);
}
