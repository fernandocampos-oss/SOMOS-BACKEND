package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.UsuarioModulo;

import java.util.Optional;

public interface UsuarioModuloRepository extends JpaRepository<UsuarioModulo, Long> {

    Optional<UsuarioModulo> findByIdUsuarioAndModulo(Integer idUsuario, String modulo);

}
