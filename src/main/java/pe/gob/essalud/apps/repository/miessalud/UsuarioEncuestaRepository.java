package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.UsuarioEncuesta;

import java.util.Optional;

public interface UsuarioEncuestaRepository extends JpaRepository<UsuarioEncuesta, Long> {

    Optional<UsuarioEncuesta> findByIdUsuarioAndIdEncuesta(Integer idUsuario, Integer idEncuesta);

}
