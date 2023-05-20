package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.essalud.apps.model.miessalud.Publicacion;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    @Query("SELECT p FROM Publicacion p WHERE p.idSede = :idSede or p.idSede = :idCentral")
    List<Publicacion> findPublicacionesBySedeAndCentral(int idSede, int idCentral);
    List<Publicacion> findPublicacionByIdSede(int idSede);

}
