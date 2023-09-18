package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.eleccion.request.VotoRequestDto;
import pe.gob.essalud.apps.dto.eleccion.response.CandidatoResponseDto;
import pe.gob.essalud.apps.dto.eleccion.response.EleccionResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.*;
import pe.gob.essalud.apps.repository.miessalud.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.EleccionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EleccionServiceImpl implements EleccionService {

    private final EleccionRepository eleccionRepository;
    private final VotanteRepository votanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final VotoRepository votoRepository;
    private final CandidatoRepository candidatoRepository;
    private final SegmentoRepository segmentoRepository;

    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Override
    public EleccionResponseDto buscarEleccionActiva() {
        List<Eleccion> eleccionList = eleccionRepository.findAll();
        if (!eleccionList.isEmpty()) {
            Eleccion eleccion = eleccionList.get(0);
            Integer idUsuario = authService.getIdUserSession();
            Usuario usuario = usuarioRepository.findById((long) idUsuario).get();
            Optional<Votante> votante = votanteRepository.findByNumeroDocumento(usuario.getNumeroDocumento());
            if (votante.isPresent() && !usuarioTieneVoto(idUsuario, eleccion.getIdEleccion())) {
                EleccionResponseDto eleccionResponseDto = modelMapper.map(eleccion, EleccionResponseDto.class);
                eleccionResponseDto.setIdSegmento(votante.get().getIdSegmento());
                eleccionResponseDto.setCandidatos(new ArrayList<>());
                for (Candidato candidato: eleccion.getCandidatos()) {
                    if (candidato.getIdSegmento().equals(votante.get().getIdSegmento())) {
                        eleccionResponseDto.getCandidatos().add(modelMapper.map(candidato, CandidatoResponseDto.class));
                    }
                }
                return eleccionResponseDto;
            }
        }
        return null;
    }

    @Override
    public void guardarVoto(VotoRequestDto votoRequestDto) {
        eleccionRepository.findById(votoRequestDto.getIdEleccion())
                .orElseThrow(() -> new ValidationException("La eleccion no se encuentra activa o registrada"));

        segmentoRepository.findById(votoRequestDto.getIdSegmento())
                .orElseThrow(() -> new ValidationException("El segmento no se encuentra registrado"));

        if (votoRequestDto.getIdCandidato() > 0) {
            Candidato candidato = candidatoRepository.findById(votoRequestDto.getIdCandidato())
                    .orElseThrow(() -> new ValidationException("La candidato no se encuentra registrado"));

            if (!candidato.getIdSegmento().equals(votoRequestDto.getIdSegmento())) {
                throw new ValidationException("El segmento del voto no coincide con el segmento del candidato");
            }
        }

        Integer idUsuario = authService.getIdUserSession();
        if (!usuarioTieneVoto(idUsuario, votoRequestDto.getIdEleccion())) {
            Voto voto = modelMapper.map(votoRequestDto, Voto.class);
            voto.setIdUsuario(authService.getIdUserSession());
            votoRepository.save(voto);
        }
    }

    public boolean usuarioTieneVoto(Integer idUsuario, Integer idEleccion) {
        Optional<Voto> voto = votoRepository.findByIdUsuarioAndIdEleccion(idUsuario, idEleccion);
        if (voto.isPresent()) {
            return true;
        }
        return false;
    }

}
