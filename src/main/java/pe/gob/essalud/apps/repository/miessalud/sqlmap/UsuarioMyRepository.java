package pe.gob.essalud.apps.repository.miessalud.sqlmap;

import org.springframework.stereotype.Repository;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;

@Repository
public interface UsuarioMyRepository {

    UsuarioResponseDto findById(long id);
    UsuarioResponseDto findByNumeroDocumento(String numeroDocumento);

}
