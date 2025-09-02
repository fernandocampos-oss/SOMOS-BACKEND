package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.PlazaSapServiceClient;
import pe.gob.essalud.apps.dto.plaza.response.PlazaResponseDto;
import pe.gob.essalud.apps.service.PlazaService;

@Service
@RequiredArgsConstructor
public class PlazaServiceImpl implements PlazaService {

    private final PlazaSapServiceClient plazaSapServiceClient;

    @Override
    public PlazaResponseDto buscarPlaza(String plaza, String nombre) {
        return plazaSapServiceClient.getPlaza(plaza, nombre);
    }

}
