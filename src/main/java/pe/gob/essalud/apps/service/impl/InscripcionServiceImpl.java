package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.Encuesta;
import pe.gob.essalud.apps.model.miessalud.Inscripcion;
import pe.gob.essalud.apps.model.miessalud.InscripcionPersona;
import pe.gob.essalud.apps.model.miessalud.UsuarioEncuesta;
import pe.gob.essalud.apps.repository.miessalud.InscripcionPersonaRepository;
import pe.gob.essalud.apps.repository.miessalud.InscripcionRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.InscripcionService;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final InscripcionPersonaRepository inscripcionPersonaRepository;

    private final AuthService authService;

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
    public void guardarInscripcion(int idInscripcion){

        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion)
                .orElseThrow(() -> new ValidationException("La encuesta no se encuentra activa o registrada"));

        Integer idUsuario = authService.getIdUserSession();
        if(!usuarioEstaInscrito(idUsuario,inscripcion.getIdInscripcion())){

            InscripcionPersona inscripcionPersona = new InscripcionPersona();
            inscripcionPersona.setIdInscripcion(idInscripcion);
            inscripcionPersona.setIdUsuario(idUsuario);
            inscripcionPersona.setEstadoActivo(true);

            inscripcionPersonaRepository.save(inscripcionPersona);
        }
    }


    public boolean usuarioEstaInscrito(Integer idUsuario, int idInscripcion) {
        Optional<InscripcionPersona> usuarioInscripcion = inscripcionPersonaRepository.findByIdUsuarioAndIdInscripcion(idUsuario, idInscripcion);
        if (usuarioInscripcion.isPresent()) {
            return true;
        }
        return false;
    }
}
