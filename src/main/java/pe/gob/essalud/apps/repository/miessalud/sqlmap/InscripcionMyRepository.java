package pe.gob.essalud.apps.repository.miessalud.sqlmap;

import org.springframework.stereotype.Repository;
import pe.gob.essalud.apps.dto.inscripcion.response.ReporteInscVotacionResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.UsuariosInscritosResponseDto;

import java.util.List;

@Repository
public interface InscripcionMyRepository {

    List<UsuariosInscritosResponseDto> getUsuariosInscritos(int idInscripcion);

    List<ReporteInscVotacionResponseDto> getVotacionesInscripcion(int idInscripcion);
}
