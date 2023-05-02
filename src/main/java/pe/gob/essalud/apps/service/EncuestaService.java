package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.encuesta.request.UsuarioEncuestaRequestDto;
import pe.gob.essalud.apps.dto.encuesta.response.EncuestaResponseDto;

public interface EncuestaService {

    EncuestaResponseDto buscarEncuestaActiva();
    void guardarRespuesta(int idEncuesta, UsuarioEncuestaRequestDto request);

}
