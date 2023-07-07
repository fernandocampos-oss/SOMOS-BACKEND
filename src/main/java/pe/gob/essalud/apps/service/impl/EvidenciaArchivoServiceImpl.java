package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.EvidenciaArchivo;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EvidenciaArchivoRepository;
import pe.gob.essalud.apps.service.EvidenciaArchivoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenciaArchivoServiceImpl implements EvidenciaArchivoService {

    private final EvidenciaArchivoRepository evidenciaArchivoRepository;

    @Override
    public EvidenciaArchivo registrar(EvidenciaArchivo evidenciaArchivo) {
        return evidenciaArchivoRepository.save(evidenciaArchivo);
    }

    @Override
    public List<EvidenciaArchivo> listar() {
        return null;
    }

    @Override
    public EvidenciaArchivo listarArchivoPorEstadoActivo(Number idEvidenciaArchivo) {
        return evidenciaArchivoRepository.listarArchivoPorEstadoActivo(idEvidenciaArchivo);
    }

    @Override
    public int eliminarArchivo(Boolean estado, Number idEvidenciaArchivo) {
        return evidenciaArchivoRepository.eliminarArchivo(estado, idEvidenciaArchivo);
    }
}
