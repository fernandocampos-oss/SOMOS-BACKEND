package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.common.constants.RoleType;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.publicacion.request.PublicacionRequestDto;
import pe.gob.essalud.apps.dto.publicacion.response.PublicacionResponseDto;
import pe.gob.essalud.apps.exceptions.ForbiddenException;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.Publicacion;
import pe.gob.essalud.apps.repository.miessalud.PublicacionRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PublicacionService;

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
        if (authService.hasRole(RoleType.TRABAJADOR)) {
            return listarPublicacionesDto(publicacionRepository.findPublicacionesBySedeAndCentral(authService.getIdSedeSession(), ID_SEDE_CENTRAL));
        }
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
        String rutaImagen = uploadPath + RUTA_IMAGENES_PUBLICACIONES + publicacion.getIdPublicacion() + FORMATO_IMAGEN_PUBLICACION;
        rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getImagenBase64());
        publicacion.setRutaImagen(rutaImagen);
        return publicacion.getIdPublicacion();
    }

    @Transactional
    @Override
    public void modificarPublicacion(long idPublicacion, PublicacionRequestDto request) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ValidationException("La publicación no existe"));

        if (!publicacion.getIdSede().equals(authService.getIdSedeSession())) {
            throw new ForbiddenException();
        }

        publicacion.setUsuarioModificacion(authService.getIdUserSession());
        publicacion.setTitulo(request.getTitulo());
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setUrlRedireccion(request.getUrlRedireccion());
        String rutaImagen = uploadPath + RUTA_IMAGENES_PUBLICACIONES + publicacion.getIdPublicacion() + FORMATO_IMAGEN_PUBLICACION;
        rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getImagenBase64());
        publicacion.setRutaImagen(rutaImagen);
        publicacionRepository.save(publicacion);
    }

    @Transactional
    @Override
    public void eliminarPublicacion(long idPublicacion) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ValidationException("La publicación no existe"));

        if (!publicacion.getIdSede().equals(authService.getIdSedeSession())) {
            throw new ForbiddenException();
        }

        publicacion.setUsuarioModificacion(authService.getIdUserSession());
        publicacion.setEsActivo(false);
        publicacionRepository.save(publicacion);
    }

    private List<PublicacionResponseDto> listarPublicacionesDto(List<Publicacion> publicacions) {
        List<PublicacionResponseDto> publicacionesDto = publicacions.stream()
                .map(p -> {
                    PublicacionResponseDto response = modelMapper.map(p, PublicacionResponseDto.class);
                    response.setImagenBase64(UploadUtil.getFileBase64(p.getRutaImagen()));
                    return response;
                })
                .collect(Collectors.toList());
        return publicacionesDto;
    }

}
