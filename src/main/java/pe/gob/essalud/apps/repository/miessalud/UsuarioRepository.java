package pe.gob.essalud.apps.repository.miessalud;

import pe.gob.essalud.apps.model.miessalud.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
	Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    Optional<Usuario> findByNumeroDocumentoAndIdEstadoUsuarioAndEsActivo(String numeroDocumento, String idEstadoUsuario, boolean esActivo);

    boolean existsByNumeroDocumentoOrCodigoPlanilla(String numeroDocumento, String codigoPlanilla);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM Usuario u " +
            "WHERE (u.numeroDocumento = ?1 OR u.codigoPlanilla = ?2) AND u.idUsuario <> ?3")
    boolean existsByNumeroDocumentoOrCodigoPlanillaAndIdUsuarioNot(String numeroDocumento, String codigoPlanilla, long idUsuario);

    List<Usuario> findAllByIdEstadoUsuarioOrderByNombres(String idEstado);
    
    @Query("SELECT u FROM Usuario u WHERE u.codigoRed = ?1 AND u.idEstadoUsuario = '02' ORDER BY u.nombres")
    List<Usuario> findAllByCodigoRed(String codigoRed);
    
    @Query("SELECT u FROM Usuario u WHERE u.numeroDocumento = ?1")
    Usuario findDocumento(String numeroDocumento);

}
