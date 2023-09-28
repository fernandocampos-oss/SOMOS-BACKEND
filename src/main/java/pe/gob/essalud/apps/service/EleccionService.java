package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.eleccion.request.VotoRequestDto;
import pe.gob.essalud.apps.dto.eleccion.response.EleccionResponseDto;

public interface EleccionService {

    EleccionResponseDto buscarEleccionActiva();
    void guardarVoto(VotoRequestDto votoRequestDto);

}
