package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.plaza.response.PlazaResponseDto;

public interface PlazaService {

    PlazaResponseDto buscarPlaza(String plaza, String nombre);

}
