package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.encuesta.response.*;
import pe.gob.essalud.apps.model.miessalud.*;
import pe.gob.essalud.apps.repository.miessalud.*;
import pe.gob.essalud.apps.service.EncuestaService;

import java.util.List;
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

    private final ModelMapper modelMapper;

    @Override
    public EncuestaResponseDto buscarEncuestaActiva() {
        List<Encuesta> encuestasActivas = encuestaRepository.findAll();
        if (!encuestasActivas.isEmpty()) {
            EncuestaResponseDto encuesta = modelMapper.map(encuestasActivas.get(0), EncuestaResponseDto.class);
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
        return null;
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

}
