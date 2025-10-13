package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EleccionServiceImpl implements EleccionService {
    private static final Logger logger = LogManager.getLogger(EleccionServiceImpl.class);

    private final EleccionRepository eleccionRepository;
    private final VotanteRepository votanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final VotoRepository votoRepository;
    private final CandidatoRepository candidatoRepository;
    private final SegmentoRepository segmentoRepository;

    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Value("${elecciones.anio}")
    private int anio;
    @Value("${elecciones.mes}")
    private int mes;
    @Value("${elecciones.dia}")
    private int dia;
    @Value("${elecciones.inicio}")
    private int horaInicio;
    @Value("${elecciones.fin}")
    private int horaFin;

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
                        CandidatoResponseDto candidatoResponseDto = modelMapper.map(candidato, CandidatoResponseDto.class);
                        Usuario usuarioCandidato = usuarioRepository.findDocumento(candidato.getNumeroDocumento());
                        if (usuarioCandidato != null) {
                            candidatoResponseDto.setIdUsuario(usuarioCandidato.getIdUsuario());
                        }
                        eleccionResponseDto.getCandidatos().add(candidatoResponseDto);
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

    @Override
    public boolean getDiaVotacion() {
        LocalDateTime fechaLocal = LocalDateTime.now(ZoneId.of("America/Lima"));
        logger.info("fechaLocal={}", fechaLocal);
        logger.info("fecha voto yml: anio={}, mes={}, dia={}", anio, mes, dia);
        LocalDate fechaVotacion = LocalDate.of(anio, mes, dia);
        if (!fechaLocal.toLocalDate().isEqual(fechaVotacion)) {
            return false;
        }
        int hora = fechaLocal.getHour();
        logger.info("hora voto yml : horaInicio={}, horaFin={}", horaInicio, horaFin);
        return hora >= horaInicio && hora < horaFin;
    }

}
