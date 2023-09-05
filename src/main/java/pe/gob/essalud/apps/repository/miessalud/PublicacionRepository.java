package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.essalud.apps.model.miessalud.Publicacion;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    @Query("SELECT p FROM Publicacion p WHERE p.alcanceRed LIKE %:codRed% or p.tipoAlcance = :tipoAlcance ORDER BY p.idPublicacion DESC")
    List<Publicacion> findPublicacionesByAlcanceRedOrTipoAlcance(String codRed, int tipoAlcance);
    @Query("SELECT p FROM Publicacion p WHERE p.alcanceRed LIKE %:codRed% ORDER BY p.idPublicacion DESC")
    List<Publicacion> findPublicacionesByAlcanceRed(String codRed);
    List<Publicacion> findPublicacionByTipoAlcanceOrderByIdPublicacionDesc(int tipoAlcance);

    @Query(value = "SELECT distinct cod_red FROM usuario_red WHERE id_usuario = :idUsuario and es_activo = true and habilitado = true", nativeQuery = true)
    List<String> findRedesAsignadasUsuario(int idUsuario);
}
