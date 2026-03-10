package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.Votante;

import java.util.List;
import java.util.Optional;

public interface VotanteRepository extends JpaRepository<Votante, Integer> {

    Optional<Votante> findByNumeroDocumento(String numeroDocumento);
    List<Votante> findByIdUsuario(Integer idUsuario);

}
