package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.InscripcionVoto;

import java.util.Optional;

public interface InscripcionVotoRepository extends JpaRepository<InscripcionVoto, Integer> {

    Optional<InscripcionVoto> findByIdUsuarioAndIdInscripcion(int idUsuario, int idInscripcion);

}
