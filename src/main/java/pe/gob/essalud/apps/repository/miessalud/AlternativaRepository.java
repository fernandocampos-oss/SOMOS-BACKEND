package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.Alternativa;

public interface AlternativaRepository extends JpaRepository<Alternativa, Integer> {
}
