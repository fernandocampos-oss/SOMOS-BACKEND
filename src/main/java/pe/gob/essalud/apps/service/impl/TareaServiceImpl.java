package pe.gob.essalud.apps.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.time.ZoneId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaValidacionDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaValidacionTransaccionalDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.TareaRepository;
import pe.gob.essalud.apps.service.TareaService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareaServiceImpl implements TareaService {

    private final TareaRepository tareaRepository;

    @Override
    public List<Tarea> listar() {
        return tareaRepository.findAll();
    }

    @Override
    public Tarea registrar(Tarea obj) {
        return tareaRepository.save(obj);
    }
    @Override
    public List<Tarea> listarTareaPorRequermientoPersonal(Number idRequerimientoPersonal) {
        return tareaRepository.listarTareaPorRequermientoPersonal(idRequerimientoPersonal);
    }

    @Transactional
    @Override
    public Integer registrarTareaNoDuplicado(TareaValidacionTransaccionalDTO dto) {
        log.info("idRequerimientoPersonal: {}", dto.getRequerimientoPersonal().getIdRequerimientoPersonal());
        if (!dto.getListTareaDTO().isEmpty()) {
            for (TareaValidacionDTO tDto : dto.getListTareaDTO()) {
                for (Tarea t : tDto.getListTarea()) {
                    log.info("idPersonal {}", tDto.getPersonal().getIdPersonal());
                    tareaRepository.registrarTareaNoDuplicado(t.getNombreTarea(),
                            t.getPlazo(), dto.getRequerimientoPersonal().getIdRequerimientoPersonal(),
                            LocalDateTime.now(ZoneId.of("America/Lima")), t.getEstadoAvance(), t.getPorcentajeAvance());
                }
            }
        }
        return dto.getRequerimientoPersonal().getIdRequerimientoPersonal();
    }

}
