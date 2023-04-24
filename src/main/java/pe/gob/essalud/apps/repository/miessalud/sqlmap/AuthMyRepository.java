package pe.gob.essalud.apps.repository.miessalud.sqlmap;

import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMyRepository {

    UserSessionDto findByUsername(String username);

}
