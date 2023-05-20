package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.publicacion.request.PublicacionRequestDto;
import pe.gob.essalud.apps.dto.publicacion.response.PublicacionResponseDto;

import java.util.List;

public interface PublicacionService {

    List<PublicacionResponseDto> listarPublicaciones();
    long crearPublicacion(PublicacionRequestDto request);
    void modificarPublicacion(long idPublicacion, PublicacionRequestDto request);
    void eliminarPublicacion(long idPublicacion);

}
