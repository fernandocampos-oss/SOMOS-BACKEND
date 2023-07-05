package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EvidenciaRepository;
import pe.gob.essalud.apps.service.EvidenciaService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenciaServiceImpl implements EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;

    @Override
    public Evidencia registrar(Evidencia evidencia) {
        return evidenciaRepository.save(evidencia);
    }

    @Override
    public List<Evidencia> listar() {
        return null;
    }
}

