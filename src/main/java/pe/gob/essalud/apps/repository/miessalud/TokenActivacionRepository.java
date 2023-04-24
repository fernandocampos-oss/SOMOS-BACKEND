package pe.gob.essalud.apps.repository.miessalud;

import pe.gob.essalud.apps.model.miessalud.TokenActivacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenActivacionRepository extends JpaRepository<TokenActivacion, Long> {

    Optional<TokenActivacion> findTopByIdUsuarioOrderByFechaCreacionDesc(long idUsuario);
    Optional<TokenActivacion> findTopByIdUsuarioAndTokenOrderByFechaCreacionDesc(long idUsuario, String token);

}
