package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoPersonal;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoPersonalRepository;
import pe.gob.essalud.apps.service.RequerimientoPersonalService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequerimientoPersonalServiceImpl implements RequerimientoPersonalService {

    private final RequerimientoPersonalRepository requerimientoPersonalRepository;

    @Override
    public RequerimientoPersonal registrar(RequerimientoPersonal obj) {
        obj.getListTarea().forEach(tarea -> {
            tarea.setRequerimientoPersonal(obj);
        });
        return requerimientoPersonalRepository.save(obj);
    }

    @Override
    public List<RequerimientoPersonal> listar() {
        return null;
    }

    @Override
    public List<RequerimientoPersonal> listarRequerimientosPorPersonal(Number idPersonal) {
        return requerimientoPersonalRepository.listarRequerimientosPorPersonal(idPersonal);
    }

    @Override
    public List<RequerimientoPersonal> validarDuplicadoRequerimientoPersonal(Number idRequerimiento, Number idPersonal) {
        return requerimientoPersonalRepository.validarDuplicadoRequerimientoPersonal(idRequerimiento, idPersonal);
    }
}
