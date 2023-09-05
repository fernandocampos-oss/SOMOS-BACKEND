package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.essalud.apps.model.miessalud.UsuarioRed;
import pe.gob.essalud.apps.model.miessalud.UsuarioRedId;

import java.util.List;

public interface UsuarioRedRepository extends JpaRepository<UsuarioRed, UsuarioRedId> {

    List<UsuarioRed> findByUsuarioIdUsuario(long idUsuario);

    UsuarioRed findByUsuarioIdUsuarioAndRedCodRed(long idUsuario, String codRed);

    @Query(value = "SELECT count(*) FROM usuario_red WHERE id_usuario = :idUsuario and cod_red = :codRed and es_activo = false", nativeQuery = true)
    Integer buscarUsuarioRedInactivo(long idUsuario, String codRed);

    @Modifying
    @Query(value = "UPDATE usuario_red SET es_activo = true WHERE id_usuario = :idUsuario and cod_red = :codRed", nativeQuery = true)
    void activarUsuarioRed(long idUsuario, String codRed);

    @Query("SELECT ur FROM UsuarioRed ur ORDER BY ur.fechaCreacion DESC")
    List<UsuarioRed> findAllOrderByFechaCreacionDesc();

}
