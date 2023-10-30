package pe.gob.essalud.apps.repository.miessalud.sqlmap;

import org.springframework.stereotype.Repository;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioInformacionResponseDto;
import pe.gob.essalud.apps.dto.usuariored.response.DatoRedResponse;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;

import java.util.List;

@Repository
public interface UsuarioRedMyRepository {

    List<UsuarioInformacionResponseDto> getUsuariosRedActivos(List<String> codRedes, Integer limite);

    List<RedPersonal> getRedesAsignadasActivas(int idUsuario);

    DatoRedResponse getDatosRedesAsignadas(String codRed);
}
