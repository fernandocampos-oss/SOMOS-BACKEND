package pe.gob.essalud.apps.repository.miessalud.encuestaformulario;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.essalud.apps.model.miessalud.Publicacion;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuesta;

import java.util.List;

public interface FormEncuestaRepository extends JpaRepository<FormEncuesta, Long> {

    List<FormEncuesta> findByIdUsuarioCreacion(Integer usuarioCreador);

    @Query("SELECT u FROM Usuario u WHERE u.idUsuario = ?1")
    Usuario findUserContesta(Long idUsuario);

//    @Query("select p from Publicacion p WHERE p.idEncuesta=:idEncuesta ")
//    Publicacion getPublicacion(@Param("idEncuesta") Integer idEncuesta);
}
