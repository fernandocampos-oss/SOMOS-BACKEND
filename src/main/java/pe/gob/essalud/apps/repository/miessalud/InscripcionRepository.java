package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.essalud.apps.model.miessalud.Inscripcion;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    Inscripcion findByIdInscripcion(int idInscripcion);
    Optional<Inscripcion> findByIdPublicacion(long idPublicacion);
    List<Inscripcion> findByVotacionAndVotoActivo(boolean votacion, boolean votoActivo);

    @Query("SELECT i FROM Inscripcion i WHERE i.idResponsable LIKE CONCAT('%,',:idUsuario,',%') OR i.idResponsable LIKE CONCAT(:idUsuario,',%') OR i.idResponsable LIKE CONCAT('%,',:idUsuario) OR i.idResponsable LIKE :idUsuario")
    List<Inscripcion> findInscripcionesByIdResponsable(String idUsuario);
}
