package pe.gob.essalud.apps.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoRepository;
import pe.gob.essalud.apps.service.RequerimientoService;

@Service
@RequiredArgsConstructor
public class RequerimientoServiceImpl implements RequerimientoService {

    private final RequerimientoRepository requerimientoRepository;

    @Override
    public Requerimiento registrar(Requerimiento obj) {
        return requerimientoRepository.save(obj);
    }

    @Override
    public List<Requerimiento> listar() {
        return requerimientoRepository.findAll();
    }

    @Override
    public int aprobarRequerimiento(Number estado, Number idRequerimiento) {
        return requerimientoRepository.aprobarRequerimiento(estado, idRequerimiento);
    }

    @Override
    public int rechazarRequerimiento(Number estado, String motivo, Number idRequerimiento) {
        return requerimientoRepository.rechazarRequerimiento(estado, motivo, idRequerimiento);
    }

    @Override
    public int derivarRequerimiento(Number estado, String motivo, Number idAreaReceptor, Number idRequerimiento) {
        return requerimientoRepository.derivarRequerimiento(estado, motivo, idAreaReceptor, idRequerimiento);
    }

}

