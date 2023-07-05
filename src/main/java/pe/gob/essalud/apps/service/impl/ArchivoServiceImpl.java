package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Archivo;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.ArchivoRepository;
import pe.gob.essalud.apps.service.ArchivoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchivoServiceImpl implements ArchivoService {

    private final ArchivoRepository archivoRepository;

    @Override
    public Archivo registrar(Archivo archivo) {
        return archivoRepository.save(archivo);
    }

    @Override
    public List<Archivo> listar() {
        return null;
    }
}
