package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.Inscripcion;

import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    Inscripcion findByIdInscripcion(int idInscripcion);
    Optional<Inscripcion> findByIdPublicacion(long idPublicacion);

}
