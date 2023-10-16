package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.inscripcion.request.InscripcionRequestDto;
import pe.gob.essalud.apps.dto.inscripcion.request.InscripcionVotoRequestDto;
import pe.gob.essalud.apps.dto.inscripcion.response.*;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.*;
import pe.gob.essalud.apps.repository.miessalud.InscripcionPersonaRepository;
import pe.gob.essalud.apps.repository.miessalud.InscripcionRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.InscripcionVotoRepository;
import pe.gob.essalud.apps.repository.miessalud.PublicacionRepository;
import pe.gob.essalud.apps.repository.miessalud.sqlmap.InscripcionMyRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.InscripcionService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements InscripcionService {

    private static final String RUTA_IMAGENES_INSCRIPCIONES = "/imagenes/inscripciones/";
    private static final String FORMATO_IMAGEN_INSCRIPCION = ".png";

    private final InscripcionRepository inscripcionRepository;
    private final InscripcionPersonaRepository inscripcionPersonaRepository;
    private final InscripcionMyRepository inscripcionMyRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    private final InscripcionVotoRepository inscripcionVotoRepository;
    private final PublicacionRepository publicacionRepository;

    private final AuthService authService;

    @Value("${upload-path}")
    private String uploadPath;

    @Override
    public InscripcionResponseDto buscarInscripcionPorId(int idInscripcion){

        Inscripcion inscripcion = inscripcionRepository.findByIdInscripcion(idInscripcion);
        if(inscripcion != null){
            InscripcionResponseDto inscripcionResponse = new InscripcionResponseDto();
            Integer idUsuario = authService.getIdUserSession();
            inscripcionResponse.setIdInscripcion(inscripcion.getIdInscripcion());
            inscripcionResponse.setDescripcion(inscripcion.getDescripcion());
            if(!usuarioEstaInscrito(idUsuario,inscripcionResponse.getIdInscripcion())){
                inscripcionResponse.setEnviado(false);
            }
            else{
                inscripcionResponse.setEnviado(true);
            }
            return inscripcionResponse;
        }
        return null;
    }

    @Override
    @Transactional
    public void guardarInscripcion(InscripcionRequestDto request){

        Inscripcion inscripcion = inscripcionRepository.findById(request.getIdInscripcion())
                .orElseThrow(() -> new ValidationException("La encuesta no se encuentra activa o registrada"));

        Integer idUsuario = authService.getIdUserSession();
        if(!usuarioEstaInscrito(idUsuario,inscripcion.getIdInscripcion())){
            if(request.getTipoInscripcion() == 1){
                InscripcionPersona inscripcionPersona = new InscripcionPersona();
                inscripcionPersona.setIdInscripcion(request.getIdInscripcion());
                inscripcionPersona.setIdUsuario(idUsuario);
                inscripcionPersona.setEstadoActivo(true);

                inscripcionPersonaRepository.save(inscripcionPersona);
            } else if (request.getTipoInscripcion() == 2) {
                for (Integer idInscrito: request.getInscritos()){
                    validarMiembroInscripcion(idInscrito, request.getIdInscripcion());
                }
                String rutaImagen = uploadPath + RUTA_IMAGENES_INSCRIPCIONES + request.getIdInscripcion() + "_" + idUsuario + FORMATO_IMAGEN_INSCRIPCION;
                rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getImagenBase64());
                for (Integer idInscrito: request.getInscritos()){
                    InscripcionPersona inscripcionPersona = new InscripcionPersona();
                    inscripcionPersona.setIdInscripcion(request.getIdInscripcion());
                    inscripcionPersona.setIdUsuario(idInscrito);
                    inscripcionPersona.setDescripcion(request.getDescripcion());
                    inscripcionPersona.setIdLider(idUsuario);
                    inscripcionPersona.setEstadoActivo(true);
                    inscripcionPersona.setRutaImagen(rutaImagen);

                    inscripcionPersonaRepository.save(inscripcionPersona);
                }
            }
        }
    }

    @Override
    public ReporteInscritosDto getUsuariosInscritos(int idInscripcion){
        ReporteInscritosDto reporte = new ReporteInscritosDto();
        Optional<InscripcionPersona> ins = inscripcionPersonaRepository.findByIdUsuarioAndIdInscripcion(347,4);
        List<UsuariosInscritosResponseDto> usuariosInscritos;
        usuariosInscritos = inscripcionMyRepository.getUsuariosInscritos(idInscripcion);
        if (idInscripcion == 2){
            usuariosInscritos.replaceAll(x->{
                if (x.getIdUsuario() == x.getIdLider()){
                    x.setRutaImagen(UploadUtil.getFileBase64(x.getRutaImagen()));
                }
                else{
                    x.setRutaImagen(null);
                }
                return x;
            });
            /*usuariosInscritos.stream().map(user->{
                        if(user.getIdUsuario() == user.getIdLider()){
                            user.setRutaImagen(UploadUtil.getFileBase64(user.getRutaImagen()));
                        }else{
                            user.setRutaImagen(UploadUtil.getFileBase64(user.getRutaImagen()));
                        }
                        return user;
                });
            Map<Object, List<UsuariosInscritosResponseDto>> usuariosGrupo =
                    usuariosInscritos.stream().collect(Collectors.groupingBy(w -> w.idLider));
            System.out.println(usuariosGrupo);*/
        }
        reporte.setIdInscripcion(idInscripcion);
        reporte.setDescripcion(inscripcionRepository.findByIdInscripcion(idInscripcion).getDescripcion());
        reporte.setInscritos(usuariosInscritos);

        return reporte;
    }

    @Override
    public List<InscripcionVotacionResponseDto> listarVotacionesActivas() {
        List<InscripcionVotacionResponseDto> inscripcionVotacionResponseDtos = new ArrayList<>();
        List<Inscripcion> inscripciones = inscripcionRepository.findByVotacionAndVotoActivo(true, true);

        for (Inscripcion inscripcion : inscripciones) {
            Integer idUsuario = authService.getIdUserSession();
            if (!usuarioTieneVoto(idUsuario, inscripcion.getIdInscripcion()) &&
                    inscripcionPerteneceAlcancePublicacion(inscripcion.getIdPublicacion())) {
                InscripcionVotacionResponseDto votacionResponseDto = new InscripcionVotacionResponseDto();
                votacionResponseDto.setIdInscripcion(inscripcion.getIdInscripcion());
                votacionResponseDto.setDescripcion(inscripcion.getDescripcion());

                List<IncripcionPersonaResponseDto> incripcionPersonaResponseDtos = new ArrayList<>();
                List<InscripcionPersona> inscripcionPersonas = inscripcionPersonaRepository.findByIdInscripcion(inscripcion.getIdInscripcion());

                for (InscripcionPersona persona: inscripcionPersonas) {
                    if (persona.getIdUsuario().equals(persona.getIdLider())) {
                        IncripcionPersonaResponseDto personaResponseDto = new IncripcionPersonaResponseDto();
                        personaResponseDto.setIdInsPersona(persona.getIdInsPersona());
                        personaResponseDto.setDescripcion(persona.getDescripcion());
                        personaResponseDto.setImagenBase64(UploadUtil.getFileBase64(persona.getRutaImagen()));
                        incripcionPersonaResponseDtos.add(personaResponseDto);
                    }
                }

                votacionResponseDto.setCandidatos(incripcionPersonaResponseDtos);
                inscripcionVotacionResponseDtos.add(votacionResponseDto);
            }
        }

        return inscripcionVotacionResponseDtos;
    }

    @Override
    public void guardarVoto(InscripcionVotoRequestDto votoRequestDto) {
        inscripcionRepository.findById(votoRequestDto.getIdInscripcion())
                .orElseThrow(() -> new ValidationException("La inscripción no se encuentra activa o registrada"));

        if (votoRequestDto.getIdCandidato() > 0) {
            inscripcionPersonaRepository.findById(votoRequestDto.getIdCandidato())
                    .orElseThrow(() -> new ValidationException("El candidato no se encuentra registrado"));
        }

        Integer idUsuario = authService.getIdUserSession();
        if (!usuarioTieneVoto(idUsuario, votoRequestDto.getIdInscripcion())) {
            InscripcionVoto voto = new InscripcionVoto();
            voto.setIdUsuario(authService.getIdUserSession());
            voto.setIdInscripcion(votoRequestDto.getIdInscripcion());
            voto.setIdCandidato(votoRequestDto.getIdCandidato());
            inscripcionVotoRepository.save(voto);
        }
    }

    @Override
    public void activarVotacion(int idInscripcion, boolean votoActivo) {
        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion)
                .orElseThrow(() -> new ValidationException("La inscripción no se encuentra activa o registrada"));

        inscripcion.setVotoActivo(votoActivo);
        inscripcionRepository.save(inscripcion);
    }

    public boolean usuarioEstaInscrito(Integer idUsuario, int idInscripcion) {
        Optional<InscripcionPersona> usuarioInscripcion = inscripcionPersonaRepository.findByIdUsuarioAndIdInscripcion(idUsuario, idInscripcion);
        if (usuarioInscripcion.isPresent()) {
            return true;
        }
        return false;
    }

    private void validarMiembroInscripcion(Integer idUsuario, Integer idInscripcion) {

        Optional<InscripcionPersona> miembroInscripcion;
        try{
            miembroInscripcion = inscripcionPersonaRepository.findByIdUsuarioAndIdInscripcion(idUsuario, idInscripcion);
        } catch(Exception e){
            e.printStackTrace();
            miembroInscripcion = Optional.empty();
        }
        if (!miembroInscripcion.isEmpty()) {
            Optional<Usuario> user = usuarioRepository.findById(Long.valueOf(idUsuario));
            throw new ValidationException("El integrante con DNI " + user.get().getNumeroDocumento() + ", ya se encuentra inscrito en otro grupo");
        }
        /*try {
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public boolean usuarioTieneVoto(Integer idUsuario, Integer idInscripcion) {
        Optional<InscripcionVoto> voto = inscripcionVotoRepository.findByIdUsuarioAndIdInscripcion(idUsuario, idInscripcion);
        if (voto.isPresent()) {
            return true;
        }
        return false;
    }

    public boolean inscripcionPerteneceAlcancePublicacion(Long idPublicacion) {
        Optional<Publicacion> publicacion = publicacionRepository.findById(idPublicacion);
        if (publicacion.isPresent()) {
            if (publicacion.get().getTipoAlcance() != null && publicacion.get().getTipoAlcance().equals(2)) {
                String codRed = authService.getCodRedSession();
                String[] redes = publicacion.get().getAlcanceRed().split(",");
                for (String red: redes) {
                    if (red.equals(codRed)) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
