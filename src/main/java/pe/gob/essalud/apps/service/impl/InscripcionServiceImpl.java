package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.ReporteInscritosDto;
import pe.gob.essalud.apps.dto.inscripcion.response.UsuariosInscritosResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.*;
import pe.gob.essalud.apps.repository.miessalud.InscripcionPersonaRepository;
import pe.gob.essalud.apps.repository.miessalud.InscripcionRepository;
import pe.gob.essalud.apps.repository.miessalud.sqlmap.InscripcionMyRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.InscripcionService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final InscripcionPersonaRepository inscripcionPersonaRepository;
    private final InscripcionMyRepository inscripcionMyRepository;

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

    @Override
    public ReporteInscritosDto getUsuariosInscritos(int idInscripcion){

        ReporteInscritosDto reporte = new ReporteInscritosDto();
        List<UsuariosInscritosResponseDto> usuariosInscritos = new ArrayList<>();

        usuariosInscritos = inscripcionMyRepository.getUsuariosInscritos(idInscripcion);

        reporte.setIdInscripcion(idInscripcion);
        reporte.setDescripcion(inscripcionRepository.findByIdInscripcion(idInscripcion).getDescripcion());
        reporte.setInscritos(usuariosInscritos);

        return reporte;
    }


    public boolean usuarioEstaInscrito(Integer idUsuario, int idInscripcion) {
        Optional<InscripcionPersona> usuarioInscripcion = inscripcionPersonaRepository.findByIdUsuarioAndIdInscripcion(idUsuario, idInscripcion);
        if (usuarioInscripcion.isPresent()) {
            return true;
        }
        return false;
    }
}
