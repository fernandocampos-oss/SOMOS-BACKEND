package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.InscripcionPersona;
import pe.gob.essalud.apps.model.miessalud.UsuarioEncuesta;

import java.util.Optional;

public interface InscripcionPersonaRepository extends JpaRepository<InscripcionPersona, Integer> {
    Optional<InscripcionPersona> findByIdUsuarioAndIdInscripcion(Integer idUsuario, Integer idInscripcion);

}
