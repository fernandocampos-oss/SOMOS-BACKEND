package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
import pe.gob.essalud.apps.model.miessalud.Inscripcion;
import pe.gob.essalud.apps.model.miessalud.Publicacion;
import pe.gob.essalud.apps.repository.miessalud.InscripcionRepository;
import pe.gob.essalud.apps.repository.miessalud.PublicacionRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PublicacionService;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicacionServiceImpl implements PublicacionService {

    private static final int TIPO_ALCANCE_SEDE_CENTRAL = 1;
    private static final int TIPO_ALCANCE_SEDE_RED = 2;
    private static final String RUTA_IMAGENES_PUBLICACIONES = "/imagenes/publicaciones/";
    private static final String FORMATO_IMAGEN_PUBLICACION = ".png";

    private final PublicacionRepository publicacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Value("${upload-path}")
    private String uploadPath;

    @Override
    public List<PublicacionResponseDto> listarPublicaciones() {
        if (authService.hasRole(RoleType.TRABAJADOR)) {
            return listarPublicacionesDto(publicacionRepository.findPublicacionesByAlcanceRedOrTipoAlcance(authService.getCodRedSession(), TIPO_ALCANCE_SEDE_CENTRAL));
        } else if (authService.hasRole(RoleType.ADMIN_SEDE)) {
            List<String> redesAsignadas = publicacionRepository.findRedesAsignadasUsuario(authService.getIdUserSession());
            List<Publicacion> publicaciones = new ArrayList<>();
            redesAsignadas.forEach(codRed -> {
                publicaciones.addAll(publicacionRepository.findPublicacionesByAlcanceRed(codRed));
            });
            publicaciones.addAll(publicacionRepository.findPublicacionByTipoAlcanceOrderByIdPublicacionDesc(TIPO_ALCANCE_SEDE_CENTRAL));
            return listarPublicacionesDto(publicaciones);
        } else if (authService.hasRole(RoleType.ADMIN_CENTRAL)) {
            return listarPublicacionesDto(publicacionRepository.findAll());
        }
        return listarPublicacionesDto(publicacionRepository.findPublicacionByTipoAlcanceOrderByIdPublicacionDesc(TIPO_ALCANCE_SEDE_CENTRAL));
    }

    @Override
    public List<PublicacionResponseDto> listarPublicacionesAdministrador() {
        if (authService.hasRole(RoleType.ADMIN_SEDE)) {
            List<String> redesAsignadas = publicacionRepository.findRedesAsignadasUsuario(authService.getIdUserSession());
            List<Publicacion> publicaciones = new ArrayList<>();
            redesAsignadas.forEach(codRed -> {
                publicaciones.addAll(publicacionRepository.findPublicacionesByAlcanceRed(codRed));
            });
            return listarPublicacionesDto(publicaciones);
        } else if (authService.hasRole(RoleType.ADMIN_CENTRAL)) {
            return listarPublicacionesDto(publicacionRepository.findAll());
        }
        return listarPublicacionesDto(publicacionRepository.findPublicacionByTipoAlcanceOrderByIdPublicacionDesc(TIPO_ALCANCE_SEDE_CENTRAL));
    }

    @Transactional
    @Override
    public long crearPublicacion(PublicacionRequestDto request) {
        Publicacion publicacion = modelMapper.map(request, Publicacion.class);
        publicacion.setUsuarioCreacion(authService.getIdUserSession());

        List<String> redesAsignadas = publicacionRepository.findRedesAsignadasUsuario(authService.getIdUserSession());
        if (authService.hasRole(RoleType.ADMIN_SEDE)) {
            publicacion.setTipoAlcance(TIPO_ALCANCE_SEDE_RED);
            publicacion.setAlcanceRed(obtenereRedesAsignadas(redesAsignadas, request.getRedes()));
        } else if (authService.hasRole(RoleType.ADMIN_CENTRAL) /*&& authService.hasAdditionalRole(RoleType.ADMIN_SEDE)*/) {
            if (request.getAlcance() == TIPO_ALCANCE_SEDE_RED) {
                publicacion.setTipoAlcance(TIPO_ALCANCE_SEDE_RED);
                publicacion.setAlcanceRed(obtenereRedesAsignadas(redesAsignadas, request.getRedes()));
            } else {
                publicacion.setTipoAlcance(TIPO_ALCANCE_SEDE_CENTRAL);
            }
        }/* else {
            publicacion.setTipoAlcance(TIPO_ALCANCE_SEDE_CENTRAL);
        }*/

        publicacion.setEsActivo(true);
        publicacion = publicacionRepository.save(publicacion);
        String rutaImagen = uploadPath + RUTA_IMAGENES_PUBLICACIONES + publicacion.getIdPublicacion() + FORMATO_IMAGEN_PUBLICACION;
        rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getImagenBase64());
        publicacion.setRutaImagen(rutaImagen);

        if (publicacion.isAnuncio()) {
            crearInscripcionAsociadaPublicacion(publicacion);
        }

        return publicacion.getIdPublicacion();
    }

    @Transactional
    @Override
    public void modificarPublicacion(long idPublicacion, PublicacionRequestDto request) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ValidationException("La publicación no existe"));

        List<String> redesAsignadas = publicacionRepository.findRedesAsignadasUsuario(authService.getIdUserSession());

        if (authService.hasRole(RoleType.ADMIN_SEDE)) {
            publicacion.setAlcanceRed(obtenereRedesAsignadas(redesAsignadas, request.getRedes()));
        } else if (authService.hasRole(RoleType.ADMIN_CENTRAL)/*&& authService.hasAdditionalRole(RoleType.ADMIN_SEDE)*/) {
            if (request.getAlcance() == TIPO_ALCANCE_SEDE_RED) {
                publicacion.setTipoAlcance(TIPO_ALCANCE_SEDE_RED);
                publicacion.setAlcanceRed(obtenereRedesAsignadas(redesAsignadas, request.getRedes()));
            } else {
                publicacion.setTipoAlcance(TIPO_ALCANCE_SEDE_CENTRAL);
            }
        }

        validarRedesAsignadas(publicacion, redesAsignadas);

        publicacion.setUsuarioModificacion(authService.getIdUserSession());
        publicacion.setTitulo(request.getTitulo());
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setUrlRedireccion(request.getUrlRedireccion());
        publicacion.setAnuncio(request.isAnuncio());
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

        List<String> redesAsignadas = publicacionRepository.findRedesAsignadasUsuario(authService.getIdUserSession());
        validarRedesAsignadas(publicacion, redesAsignadas);

        if (authService.hasRole(RoleType.ADMIN_SEDE)) {
            String[] redes = publicacion.getAlcanceRed().split(",");
            for (String red: redes) {
                if (!redesAsignadas.contains(red)) {
                    throw new ValidationException("La publicación también pertenece a una red no asignada");
                }
            }
        }

        publicacion.setUsuarioModificacion(authService.getIdUserSession());
        publicacion.setEsActivo(false);
        publicacionRepository.save(publicacion);
    }

    private void validarRedesAsignadas(Publicacion publicacion, List<String> redesAsignadas) {
        if (!authService.hasRole(RoleType.ADMIN_CENTRAL)) {
            AtomicBoolean estaAsignado = new AtomicBoolean(false);
            redesAsignadas.forEach(codRed -> {
                if (publicacion.getAlcanceRed().contains(codRed)) {
                    estaAsignado.set(true);
                }
            });
            if (!estaAsignado.get() || publicacion.getTipoAlcance() != TIPO_ALCANCE_SEDE_RED) {
                throw new ForbiddenException();
            }
        }
    }

    private List<PublicacionResponseDto> listarPublicacionesDto(List<Publicacion> publicacions) {
        List<PublicacionResponseDto> publicacionesDto = publicacions.stream()
                .collect(Collectors.toMap(Publicacion::getIdPublicacion, Function.identity(), (existing, replacement) -> existing))
                .values().stream()
                .map(p -> {
                    PublicacionResponseDto response = modelMapper.map(p, PublicacionResponseDto.class);
                    response.setImagenBase64(UploadUtil.getFileBase64(p.getRutaImagen()));
                    if (StringUtils.isNotBlank(p.getAlcanceRed())) {
                        response.setRedes(Arrays.asList(p.getAlcanceRed().split(",")));
                    }
                    if (p.isAnuncio()) {
                        response.setIdInscripcion(inscripcionRepository.findByIdPublicacion(p.getIdPublicacion()).orElse(new Inscripcion()).getIdInscripcion());
                    }
                    return response;
                })
                .sorted(Comparator.comparingInt(PublicacionResponseDto::getIdPublicacion).reversed())
                .collect(Collectors.toList());

        return publicacionesDto;
    }

    private String obtenereRedesAsignadas(List<String> redesAsignadas, List<String> redes) {
        if (authService.hasRole(RoleType.ADMIN_SEDE)) {
            if (redesAsignadas.isEmpty()) {
                throw new ValidationException("No tiene redes asignadas");
            }
            for (String red: redes) {
                if (!redesAsignadas.contains(red)) {
                    throw new ValidationException("La publicación contiene una red no asignada");
                }
            }
        }
        return StringUtils.join(redes, ",");
    }

    public void crearInscripcionAsociadaPublicacion(Publicacion publicacion) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setDescripcion(publicacion.getTitulo());
        inscripcion.setEsActivo(true);
        inscripcion.setIdResponsable(publicacion.getUsuarioCreacion());
        inscripcion.setIdPublicacion(publicacion.getIdPublicacion());
        inscripcionRepository.save(inscripcion);
    }

}
