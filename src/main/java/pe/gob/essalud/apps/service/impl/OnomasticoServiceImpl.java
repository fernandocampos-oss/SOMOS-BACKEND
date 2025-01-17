package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.OnomasticoRepository;
import pe.gob.essalud.apps.service.OnomasticoService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Override
    public Usuario findUsuarioByNumDocAndEstado(String numeroDocumento) {
        log.info("sssssss [{}]", numeroDocumento);
        return onomasticoRepository.findUsuarioByNumDocAndEstado(numeroDocumento);
    }

}
