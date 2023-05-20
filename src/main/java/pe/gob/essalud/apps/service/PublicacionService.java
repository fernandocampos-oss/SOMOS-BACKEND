package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.publicacion.request.PublicacionRequestDto;
import pe.gob.essalud.apps.dto.publicacion.response.PublicacionResponseDto;

import java.util.List;

public interface PublicacionService {

    List<PublicacionResponseDto> listarPublicaciones();
    List<PublicacionResponseDto> listarPublicacionesAdmin();
    long crearPublicacion(PublicacionRequestDto request);
    void modificarPublicacionDatos(long idPublicacion, PublicacionRequestDto request);
    void modificarPublicacionImagen(long idPublicacion, String imagenBase64);
    void eliminarPublicacion(long idPublicacion);

}
