package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.formencuesta.reponse.FormEncuestaResponseDto;
import pe.gob.essalud.apps.dto.formencuesta.reponse.FormRespuestaDto;
import pe.gob.essalud.apps.dto.formencuesta.request.FormRegisterRespuestaRequestDto;
import pe.gob.essalud.apps.model.miessalud.Publicacion;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuesta;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuestaTrabajador;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormPregunta;
import pe.gob.essalud.apps.repository.miessalud.encuestaformulario.FormEncuestaTrabajadorRepository;
import pe.gob.essalud.apps.repository.miessalud.encuestaformulario.FormPreguntaRepository;
import pe.gob.essalud.apps.repository.miessalud.encuestaformulario.FormEncuestaRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.FormularioEncuestaService;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormularioEncuestaServiceImpl implements FormularioEncuestaService {

    private final FormEncuestaRepository formEncuestaRepository;
    private final FormPreguntaRepository formPreguntaRepository;
    private final AuthService authService;
    private final FormEncuestaTrabajadorRepository formEncuestaTrabajadorRepository;

    @Override
    public List<FormEncuestaResponseDto> listEncuestaByUsuarioCreacion() {
        List<FormEncuestaResponseDto> listDto = new ArrayList<>();

        List<FormEncuesta> list = formEncuestaRepository.findByIdUsuarioCreacion(authService.getIdUserSession());
        log.info("list: [{}-{}]", authService.getIdUserSession(), list.size());
        if (!list.isEmpty()) {
            for (FormEncuesta i : list) {
                FormEncuestaResponseDto model = new FormEncuestaResponseDto();
                model.setIdFormEncuesta(i.getIdFormEncuesta());
                Publicacion publicacion = formEncuestaRepository.getPublicacion(i.getIdFormEncuesta());
                if (publicacion != null) {
                    log.info("publicacion: [{}]", publicacion.getTitulo());
                    model.setPublicacionNombre(publicacion.getTitulo());
                }
                model.setIdUsuarioCreacion(i.getIdUsuarioCreacion());

                //consulta por cada encuesta - los usuarios encuestados
                List<FormRespuestaDto> bloquetrabajadores = new ArrayList<>();
                List<Integer> listTrabajadoresEncuestados = formPreguntaRepository.listarTrabajadoresEncuestadosPorEncuesta(i.getIdFormEncuesta());
                for (Integer j : listTrabajadoresEncuestados) {
                    log.info("encuesta-usuario: [{}-{}]", i.getIdFormEncuesta(), j);
                    List<FormEncuestaTrabajador> listRespuestaTrabajador = formPreguntaRepository.listarRespuestasPorEncuestaYtrabajador(i.getIdFormEncuesta(), j);

                    FormRespuestaDto respta = new FormRespuestaDto();

                    Usuario userContesta = formEncuestaRepository.findUserContesta((long) j);
                    respta.setNombreUsuarioContesta(userContesta.getNombres() + " " + userContesta.getApellidos());

                    respta.setListPreguntas(listRespuestaTrabajador);
                    bloquetrabajadores.add(respta);
                }
                model.setListUsuarios(bloquetrabajadores);

                listDto.add(model);
            }
        }
        return listDto;
    }

    @Override
    public List<FormPregunta> listarPreguntasByIdEncuesta(Integer id) {
        return formPreguntaRepository.listarPreguntasByIdEncuesta(id);
    }

    @Override
    public void registrarRespuesta(FormRegisterRespuestaRequestDto dto) {
        for (FormPregunta i : dto.getListPregunta()) {
            FormEncuestaTrabajador model = new FormEncuestaTrabajador();
            log.info("id-pregunta: [{}]", i.getIdFormPregunta());
            model.setIdFormEncuesta(dto.getIdFormEncuesta());
            FormPregunta pregunta = new FormPregunta();
            pregunta.setIdFormPregunta(i.getIdFormPregunta());
            model.setFormPregunta(pregunta);
            model.setIdTrabajador(authService.getIdUserSession());
            if (i.getRespuesta().equals("Satisfecho")) {
                model.setSatisfecho("Si");
                model.setPorMejorar(null);
                model.setInsatisfecho(null);
            }
            if (i.getRespuesta().equals("Por mejorar")) {
                model.setSatisfecho(null);
                model.setPorMejorar("Si");
                model.setInsatisfecho(null);
            }
            if (i.getRespuesta().equals("Insatisfecho")) {
                model.setSatisfecho(null);
                model.setPorMejorar(null);
                model.setInsatisfecho("Si");
            }
            model.setFinalizado(true);
            formEncuestaTrabajadorRepository.save(model);
        }
    }

    @Override
    public boolean evaluarEncuestaFinalizado(Integer idEncuesta) {
        log.info("idencuesta-user: [{}-{}]", idEncuesta, authService.getIdUserSession());
        boolean validacion;
        Optional<Boolean>  esFinalizado = formPreguntaRepository.evaluarEncuestaFinalizado(idEncuesta, authService.getIdUserSession());
        if (esFinalizado.isPresent()) {
            validacion = true;
        } else {
            validacion = false;
        }
        return validacion;
    }

}
