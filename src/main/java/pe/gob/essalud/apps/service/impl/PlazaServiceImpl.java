package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.PlazaSapServiceClient;
import pe.gob.essalud.apps.dto.plaza.response.PlazaResponseDto;
import pe.gob.essalud.apps.service.PlazaService;
import pe.gob.essalud.apps.service.UsuarioService;

@Service
@RequiredArgsConstructor
public class PlazaServiceImpl implements PlazaService {

    private final PlazaSapServiceClient plazaSapServiceClient;
    private final UsuarioService usuarioService;

    @Override
    public PlazaResponseDto buscarPlaza(String plaza, String nombre) {
        if (usuarioService.usuarioTienePermisoModulo("PLAZAS")) {
            return plazaSapServiceClient.getPlaza(plaza, nombre);
        }
        return null;
    }

}
