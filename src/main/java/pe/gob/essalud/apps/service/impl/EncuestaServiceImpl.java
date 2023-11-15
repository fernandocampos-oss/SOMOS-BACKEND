package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.encuesta.request.UsuarioEncuestaRequestDto;
import pe.gob.essalud.apps.dto.encuesta.request.UsuarioEncuestaRespuestaRequestDto;
import pe.gob.essalud.apps.dto.encuesta.response.*;
import pe.gob.essalud.apps.dto.inscripcion.response.ReporteInscritosDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.*;
import pe.gob.essalud.apps.repository.miessalud.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.EncuestaService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncuestaServiceImpl implements EncuestaService {

    private final EncuestaRepository encuestaRepository;
    private final AlternativaRepository alternativaRepository;
    private final AreaPersonalRepository areaPersonalRepository;
    private final GrupoPersonalRepository grupoPersonalRepository;
    private final SedeRepository sedeRepository;
    private final TiempoServicioRepository tiempoServicioRepository;
    private final UsuarioEncuestaRepository usuarioEncuestaRepository;
    private final UsuarioEncuestaRespuestaRepository usuarioEncuestaRespuestaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RedPersonalRepository redPersonalRepository;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Override
    public EncuestaResponseDto buscarEncuestaActiva() {
        List<Encuesta> encuestasActivas = encuestaRepository.findAll();
        if (!encuestasActivas.isEmpty()) {
            EncuestaResponseDto encuesta = modelMapper.map(encuestasActivas.get(0), EncuestaResponseDto.class);
            Integer idUsuario = authService.getIdUserSession();
            if (!usuarioTieneEncuesta(idUsuario, encuesta.getIdEncuesta())) {
                for (PreguntaResponseDto pregunta: encuesta.getPreguntas()) {
                    pregunta.setAlternativas(findAllAlternativas());
                }
                DatosDemograficosResponseDto datosDemograficos = new DatosDemograficosResponseDto();
                datosDemograficos.setSedes(findAllSedes());
                datosDemograficos.setAreasPersonales(findAllAreasPersonales());
                datosDemograficos.setGruposPersonales(findAllGruposPersonales());
                datosDemograficos.setTiemposDeServicios(findAllTiemposDeServicios());
                encuesta.setDatosDemograficos(datosDemograficos);
                return encuesta;
            }
        }
        return null;
    }

    @Transactional
    @Override
    public void guardarRespuesta(int idEncuesta, UsuarioEncuestaRequestDto request) {
        Encuesta encuesta = encuestaRepository.findById(idEncuesta)
                .orElseThrow(() -> new ValidationException("La encuesta no se encuentra activa o registrada"));

        Integer idUsuario = authService.getIdUserSession();
        if (!usuarioTieneEncuesta(idUsuario, encuesta.getIdEncuesta())) {
            UsuarioEncuesta usuarioEncuesta = new UsuarioEncuesta();
            usuarioEncuesta.setIdEncuesta(encuesta.getIdEncuesta());
            usuarioEncuesta.setIdUsuario(idUsuario);
            usuarioEncuesta.setIdSede(request.getIdSede());
            usuarioEncuesta.setIdAreaPersonal(request.getIdAreaPersonal());
            usuarioEncuesta.setIdGrupoPersonal(request.getIdGrupoPersonal());
            usuarioEncuesta.setIdTiempoServicio(request.getIdTiempoServicio());
            usuarioEncuesta = usuarioEncuestaRepository.save(usuarioEncuesta);

            for (UsuarioEncuestaRespuestaRequestDto respuesta: request.getRespuestas()) {
                UsuarioEncuestaRespuesta usuarioEncuestaRespuesta = new UsuarioEncuestaRespuesta();
                usuarioEncuestaRespuesta.setIdUsuarioEncuesta(usuarioEncuesta.getIdUsuarioEncuesta());
                usuarioEncuestaRespuesta.setIdPregunta(respuesta.getIdPregunta());
                usuarioEncuestaRespuesta.setIdAlternativa(respuesta.getIdAlternativa());
                usuarioEncuestaRespuestaRepository.save(usuarioEncuestaRespuesta);
            }
        }
    }

    public boolean usuarioTieneEncuesta(Integer idUsuario, Integer idEncuesta) {
        Optional<UsuarioEncuesta> usuarioEncuesta = usuarioEncuestaRepository.findByIdUsuarioAndIdEncuesta(idUsuario, idEncuesta);
        if (usuarioEncuesta.isPresent()) {
            return true;
        }
        return false;
    }

    public List<AlternativaResponseDto> findAllAlternativas() {
        return alternativaRepository.findAll()
                .stream()
                .map(x -> modelMapper.map(x, AlternativaResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<AreaPersonalResponseDto> findAllAreasPersonales() {
        return areaPersonalRepository.findAll()
                .stream()
                .map(x -> modelMapper.map(x, AreaPersonalResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<GrupoPersonalResponseDto> findAllGruposPersonales() {
        return grupoPersonalRepository.findAll()
                .stream()
                .map(x -> modelMapper.map(x, GrupoPersonalResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<SedeResponseDto> findAllSedes() {
        return sedeRepository.findAll()
                .stream()
                .map(x -> modelMapper.map(x, SedeResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<TiempoServicioResponseDto> findAllTiemposDeServicios() {
        return tiempoServicioRepository.findAll()
                .stream()
                .map(x -> modelMapper.map(x, TiempoServicioResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReporteEncuestaResponseDto obtenerResultadosEncuesta(int idEncuesta){
        Encuesta encuestaDato = encuestaRepository.findById(idEncuesta).orElseThrow(() -> new ValidationException("La encuesta no se encuentra activa o registrada"));

        ReporteEncuestaResponseDto reporte = new ReporteEncuestaResponseDto();

        reporte.setIdEncuesta(idEncuesta);
        reporte.setNombreEncuesta(encuestaDato.getDescripcion());

        List<Pregunta> encuestaPreguntasDato = encuestaDato.getPreguntas();
        List<UsuarioEncuesta> respuestasUsuarios = usuarioEncuestaRepository.findByIdEncuesta(idEncuesta);
        reporte.setCantidadRespuestas(respuestasUsuarios.size());

        List<ReportePreguntaResponseDto> preguntasResponse = new ArrayList<>();
        for (Pregunta pregUnitaria : encuestaPreguntasDato){
            ReportePreguntaResponseDto reportePregunta = new ReportePreguntaResponseDto();
            reportePregunta.setIdPregunta(pregUnitaria.getIdPregunta());
            reportePregunta.setDescripcion(pregUnitaria.getDescripcion());

            /*De momento las todas las alternativas están ancladas a todas las preguntas y no hay asignacion, de momento se esta haciendo así*/
            List<Alternativa> opciones = alternativaRepository.findAll();
            List<ReporteAlternativaResponseDto> alternativasResponse = new ArrayList<>();
            for (Alternativa alter : opciones){
                ReporteAlternativaResponseDto reporteAlternativa = new ReporteAlternativaResponseDto();
                List<UsuarioEncuestaRespuesta> respuestas = usuarioEncuestaRespuestaRepository.findByIdPreguntaAndIdAlternativa(pregUnitaria.getIdPregunta(), alter.getIdAlternativa());
                reporteAlternativa.setIdAlternativa(alter.getIdAlternativa());
                reporteAlternativa.setDescripcion(alter.getDescripcion());
                reporteAlternativa.setCantidadEleccion(respuestas.size());
                List<Long> usuariosQueEligieron = new ArrayList<>();
                for (UsuarioEncuestaRespuesta resp : respuestas){
                    usuariosQueEligieron.add(resp.getIdUsuarioEncuesta());
                }
                reporteAlternativa.setUsuariosEleccion(usuariosQueEligieron);
                alternativasResponse.add(reporteAlternativa);
            }
            reportePregunta.setAlternativas(alternativasResponse);
            preguntasResponse.add(reportePregunta);
        }
        reporte.setPreguntas(preguntasResponse);

        List<ReporteUsuarioEncuestaResponseDto> userLista = new ArrayList<>();
        for (UsuarioEncuesta respuestas : respuestasUsuarios){
            ReporteUsuarioEncuestaResponseDto userResponse = new ReporteUsuarioEncuestaResponseDto();
            Usuario userById = usuarioRepository.getReferenceById(Long.valueOf(respuestas.getIdUsuario()));
            userResponse.setIdUsuarioEncuesta(respuestas.getIdUsuarioEncuesta());
            userResponse.setIdUsuario(userById.getIdUsuario());
            userResponse.setNombreCompleto(userById.getNombres() + ' ' + userById.getApellidos());
            userResponse.setNumeroDocumento(userById.getNumeroDocumento());
            userResponse.setCodigoPlanilla(userById.getCodigoPlanilla());
            if (redPersonalRepository.findById(userById.getCodigoRed()).isPresent()){
                userResponse.setRed(redPersonalRepository.findById(userById.getCodigoRed()).get().getDescripcion());
            }
            else {
                userResponse.setRed("Red no registrada actualmente");
            }
            if (unidadOrganizativaRepository.findById(userById.getCodigoUnidad()).isPresent()){
                userResponse.setUnidadOrganica(unidadOrganizativaRepository.findById(userById.getCodigoUnidad()).get().getDescripcion());
            }
            else {
                userResponse.setUnidadOrganica("Unidad orgánica no registrada actualmente");
            }
            userResponse.setCargo(userById.getCargo());
            userResponse.setRegimen(userById.getRegimen());
            userResponse.setNumeroCelular(userById.getNumeroCelular());
            userResponse.setCorreo(userById.getCorreo());

            userLista.add(userResponse);
        }
        reporte.setUsuarios(userLista);

        return reporte;
    }
}
