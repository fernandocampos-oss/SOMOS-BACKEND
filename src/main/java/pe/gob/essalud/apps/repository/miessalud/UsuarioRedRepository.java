package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.essalud.apps.model.miessalud.UsuarioRed;
import pe.gob.essalud.apps.model.miessalud.UsuarioRedId;

import java.util.List;

public interface UsuarioRedRepository extends JpaRepository<UsuarioRed, UsuarioRedId> {

    List<UsuarioRed> findByUsuarioIdUsuario(long idUsuario);

    @Query("SELECT ur FROM UsuarioRed ur ORDER BY ur.fechaCreacion DESC")
    List<UsuarioRed> findAllOrderByFechaCreacionDesc();

}
