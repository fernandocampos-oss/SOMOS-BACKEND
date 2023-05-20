package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.publicacion.request.PublicacionRequestDto;
import pe.gob.essalud.apps.dto.publicacion.response.PublicacionResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.Publicacion;
import pe.gob.essalud.apps.repository.miessalud.PublicacionRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PublicacionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicacionServiceImpl implements PublicacionService {

    private static final int ID_SEDE_CENTRAL = 1;
    private static final String RUTA_IMAGENES_PUBLICACIONES = "/imagenes/publicaciones/";
    private static final String FORMATO_IMAGEN_PUBLICACION = ".png";

    private final PublicacionRepository publicacionRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Value("${upload-path}")
    private String uploadPath;

    @Override
    public List<PublicacionResponseDto> listarPublicaciones() {
        return listarPublicacionesDto(publicacionRepository.findPublicacionesBySedeAndCentral(authService.getIdSedeSession(), ID_SEDE_CENTRAL));
    }

    @Override
    public List<PublicacionResponseDto> listarPublicacionesAdmin() {
        return listarPublicacionesDto(publicacionRepository.findPublicacionByIdSede(authService.getIdSedeSession()));
    }

    @Transactional
    @Override
    public long crearPublicacion(PublicacionRequestDto request) {
        Publicacion publicacion = modelMapper.map(request, Publicacion.class);
        publicacion.setUsuarioCreacion(authService.getIdUserSession());
        publicacion.setIdSede(authService.getIdSedeSession());
        publicacion.setEsActivo(true);
        publicacion = publicacionRepository.save(publicacion);
        String rutaImagen = guardarImagenPublicacion(publicacion.getIdPublicacion(), request.getImagenBase64());
        publicacion.setRutaImagen(rutaImagen);
        return publicacion.getIdPublicacion();
    }

    @Transactional
    @Override
    public void modificarPublicacionDatos(long idPublicacion, PublicacionRequestDto request) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ValidationException("La publicación no se encuentra registrada"));

        if (!publicacion.getIdSede().equals(authService.getIdSedeSession())) {
            throw new ValidationException("La sede del usuario no coincide con la sede de la publicación");
        }

        publicacion.setUsuarioModificacion(authService.getIdUserSession());
        publicacion.setTitulo(request.getTitulo());
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setUrlRedireccion(request.getUrlRedireccion());
        publicacionRepository.save(publicacion);
    }

    @Transactional
    @Override
    public void modificarPublicacionImagen(long idPublicacion, String imagenBase64) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ValidationException("La publicación no se encuentra registrada"));

        if (!publicacion.getIdSede().equals(authService.getIdSedeSession())) {
            throw new ValidationException("La sede del usuario no coincide con la sede de la publicación");
        }

        publicacion.setUsuarioModificacion(authService.getIdUserSession());
        String rutaImagen = guardarImagenPublicacion(publicacion.getIdPublicacion(), imagenBase64);
        publicacion.setRutaImagen(rutaImagen);
        publicacionRepository.save(publicacion);
    }

    @Transactional
    @Override
    public void eliminarPublicacion(long idPublicacion) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ValidationException("La publicación no se encuentra registrada"));

        if (!publicacion.getIdSede().equals(authService.getIdSedeSession())) {
            throw new ValidationException("La sede del usuario no coincide con la sede de la publicación");
        }

        publicacion.setUsuarioModificacion(authService.getIdUserSession());
        publicacion.setEsActivo(false);
        publicacionRepository.save(publicacion);
    }

    private List<PublicacionResponseDto> listarPublicacionesDto(List<Publicacion> publicacions) {
        List<PublicacionResponseDto> publicacionesDto = publicacions.stream()
                .map(p -> {
                    PublicacionResponseDto response = modelMapper.map(p, PublicacionResponseDto.class);
                    response.setImagenBase64(obtenerImagenPublicacion(p.getRutaImagen()));
                    return response;
                })
                .collect(Collectors.toList());
        return publicacionesDto;
    }

    private String guardarImagenPublicacion(long idPublicacion, String imagenBase64) {
        String rutaImagen;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(imagenBase64);
            rutaImagen = uploadPath + RUTA_IMAGENES_PUBLICACIONES + idPublicacion + FORMATO_IMAGEN_PUBLICACION;
            FileUtils.writeByteArrayToFile(new File(rutaImagen), decodedBytes);
        } catch (IOException e) {
            rutaImagen = "";
            e.printStackTrace();
        }
        return rutaImagen;
    }

    private String obtenerImagenPublicacion(String rutaImagen) {
        String imagenBase64;
        try {
            Path imageFilePath = Path.of(rutaImagen);
            byte[] imageBytes = Files.readAllBytes(imageFilePath);
            imagenBase64 = Base64.getEncoder().encodeToString(imageBytes);;
        } catch (IOException e) {
            imagenBase64 = "";
            e.printStackTrace();
        }
        return imagenBase64;
    }

}
