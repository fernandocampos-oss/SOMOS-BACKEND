package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.Voto;

import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Integer> {

    Optional<Voto> findByIdUsuarioAndIdEleccion(int idUsuario, int idEleccion);

}
