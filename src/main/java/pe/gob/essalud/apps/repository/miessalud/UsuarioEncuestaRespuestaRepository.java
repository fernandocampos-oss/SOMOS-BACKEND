package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.UsuarioEncuestaRespuesta;

import java.util.List;

public interface UsuarioEncuestaRespuestaRepository extends JpaRepository<UsuarioEncuestaRespuesta, Long> {

    UsuarioEncuestaRespuesta findByIdUsuarioEncuesta(Long idUsuarioEncuesta);

    List<UsuarioEncuestaRespuesta> findByIdPreguntaAndIdAlternativa(Integer idPregunta, Integer idAlternativa);
}
