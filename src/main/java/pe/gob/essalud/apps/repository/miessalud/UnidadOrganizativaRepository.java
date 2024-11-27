package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;

import java.util.List;

public interface UnidadOrganizativaRepository extends JpaRepository<UnidadOrganizativa, String> {
    List<UnidadOrganizativa> findAllByCodPadre(String codPadre);
    UnidadOrganizativa findFirstByCodUnidad(String codUnidad);
}
