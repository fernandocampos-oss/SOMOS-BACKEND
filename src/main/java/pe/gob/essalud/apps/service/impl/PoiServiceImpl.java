package pe.gob.essalud.apps.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Poi;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.PoiRepository;
import pe.gob.essalud.apps.service.PoiService;

@Service
@RequiredArgsConstructor
public class PoiServiceImpl implements PoiService {

    private final PoiRepository poiRepository;

    @Override
    public Poi registrar(Poi obj) {
//        return poiRepository.save(obj);
        return null;
    }

    @Override
    public List<Poi> listar() {
        return poiRepository.findAll();
    }

}