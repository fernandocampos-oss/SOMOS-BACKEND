package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.GrupoPersonal;

public interface GrupoPersonalRepository extends JpaRepository<GrupoPersonal, Integer> {
}
