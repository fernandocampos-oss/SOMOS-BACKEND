package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.formencuesta.reponse.FormEncuestaResponseDto;
import pe.gob.essalud.apps.dto.formencuesta.request.FormRegisterRespuestaRequestDto;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuestaTrabajador;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormPregunta;

import java.util.List;

public interface FormularioEncuestaService {
    List<FormEncuestaResponseDto> listEncuestaByUsuarioCreacion();

    List<FormPregunta> listarPreguntasByIdEncuesta(Integer id);

    void registrarRespuesta(FormRegisterRespuestaRequestDto dto);

    boolean evaluarEncuestaFinalizado(Integer idEncuesta);
}
