package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.repository.miessalud.OnomasticoRepository;
import pe.gob.essalud.apps.service.OnomasticoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnomasticoServiceImpl implements OnomasticoService {

    private final OnomasticoRepository onomasticoRepository;

    @Override
    public List<Onomastico> findAllOnomasticos() {
        return onomasticoRepository.findAll();
    }

    @Override
    public List<Onomastico> findAllOnomasticosByMes(String mes) {
        return onomasticoRepository.findByMes(mes);
    }

    @Override
    public List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia) {
        return onomasticoRepository.findByMesAndDia(mes, dia);
    }

}
