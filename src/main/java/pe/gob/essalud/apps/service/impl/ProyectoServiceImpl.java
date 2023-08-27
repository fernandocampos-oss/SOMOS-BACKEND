package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.proyecto.request.*;
import pe.gob.essalud.apps.dto.usuariored.response.UsuarioDataResponse;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.*;
import pe.gob.essalud.apps.repository.miessalud.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.ProyectoService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoServiceImpl implements ProyectoService {

    private static final String RUTA_IMAGENES_PROYECTO_GRUPO = "/imagenes/proyectos/grupos/";
    private static final String RUTA_PDF_PROYECTO_IMPLEMENTACION = "/pdf/proyectos/implementaciones/";
    private static final String FORMATO_IMAGEN_PROYECTO_GRUPO = ".png";
    private static final String FORMATO_PDF_PROYECTO_IMPLEMENTACION = ".pdf";
    private static final String SEPARADOR = "-";
    private static final String COD_RED_SEDE_CENTRAL_AFESSALUD="0100";
    private static final String DESCRIPCION_RED_SEDE_CENTRAL_AFESSALUD="SEDE CENTRAL LIMA-AFESSALUD";
    private static final String COD_RED_SEDE_CENTRAL_FONDOSALUD="0200";
    private static final String DESCRIPCION_RED_SEDE_CENTRAL_FONDOSALUD="SEDE CENTRAL LIMA-Fondo Salud";

    private final ProyectoRepository proyectoRepository;
    private final ProyectoGrupoRepository proyectoGrupoRepository;
    private final ProyectoMiembroRepository proyectoMiembroRepository;
    private final ProyectoDescripcionRepository proyectoDescripcionRepository;
    private final ProyectoImplementacionRepository proyectoImplementacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RedPersonalRepository redPersonalRepository;

    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Value("${upload-path}")
    private String uploadPath;

    @Override
    public List<ProyectoRequest> listarProyectos() {
        return proyectoRepository.findByUsuarioCreacion(authService.getIdUserSession()).stream()
                .map(p -> {
                    ProyectoRequest proyecto = new ProyectoRequest();
                    proyecto.setIdProyecto(p.getIdProyecto());
                    proyecto.setEnviado(p.isEnviado());

                    ProyectoGrupoRequest grupo = modelMapper.map(p.getProyectoGrupo(), ProyectoGrupoRequest.class);
                    grupo.setImagenBase64(UploadUtil.getFileBase64(p.getProyectoGrupo().getRutaImagen()));

                    List<ProyectoMiembroRequest> miembros = p.getProyectoMiembros().stream()
                                    .map(m -> modelMapper.map(m, ProyectoMiembroRequest.class))
                                    .collect(Collectors.toList());
                    grupo.setMiembros(miembros);
                    proyecto.setGrupo(grupo);

                    ProyectoDescripcionRequest descripcion = modelMapper.map(p.getProyectoDescripcion(), ProyectoDescripcionRequest.class);
                    proyecto.setDescripcion(descripcion);

                    ProyectoImplementacionRequest implementacion = modelMapper.map(p.getProyectoImplementacion(), ProyectoImplementacionRequest.class);
                    if (StringUtils.isNotBlank(p.getProyectoImplementacion().getEnfoque())) {
                        String[] enfoques = p.getProyectoImplementacion().getEnfoque().split(SEPARADOR);
                        List<String> listaEnfoques = new ArrayList<>();
                        for (String enfoque : enfoques) {
                            listaEnfoques.add(enfoque);
                        }
                        implementacion.setEnfoques(listaEnfoques);
                    } else {
                        implementacion.setEnfoques(new ArrayList<>());
                    }
                    implementacion.setArchivoBase64(UploadUtil.getFileBase64(p.getProyectoImplementacion().getRutaArchivo()));

                    proyecto.setImplementacion(implementacion);
                    return proyecto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ProyectoRequest guardarProyecto(ProyectoRequest request) {
        if (request.getIdProyecto() == 0) {

            if (!proyectoRepository.findByUsuarioCreacion(authService.getIdUserSession()).isEmpty()) {
                throw new ValidationException("El usuario ya es lider en otro proyecto");
            }

            Proyecto proyecto = new Proyecto();
            proyecto.setEnviado(request.isEnviado());
            proyecto.setEsActivo(true);
            proyecto.setUsuarioCreacion(authService.getIdUserSession());
            proyecto = proyectoRepository.save(proyecto);

            ProyectoGrupo proyectoGrupo = modelMapper.map(request.getGrupo(), ProyectoGrupo.class);
            proyectoGrupo.setProyecto(proyecto);
            String rutaImagen = uploadPath + RUTA_IMAGENES_PROYECTO_GRUPO + proyecto.getIdProyecto() + FORMATO_IMAGEN_PROYECTO_GRUPO;
            rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getGrupo().getImagenBase64());
            proyectoGrupo.setRutaImagen(rutaImagen);
            proyectoGrupo = proyectoGrupoRepository.save(proyectoGrupo);
            request.getGrupo().setIdProyectoGrupo(proyectoGrupo.getIdProyectoGrupo());

            List<ProyectoMiembroRequest> miembrosRequest = new ArrayList<>();
            for (ProyectoMiembroRequest proyectoMiembroRequest: request.getGrupo().getMiembros()) {
                validarMiembro(proyectoMiembroRequest);
                ProyectoMiembro proyectoMiembro = modelMapper.map(proyectoMiembroRequest, ProyectoMiembro.class);
                proyectoMiembro.setProyecto(proyecto);
                proyectoMiembro = proyectoMiembroRepository.save(proyectoMiembro);
                proyectoMiembroRequest.setIdProyectoMiembro(proyectoMiembro.getIdProyectoMiembro());
                miembrosRequest.add(proyectoMiembroRequest);
            }
            request.getGrupo().setMiembros(miembrosRequest);

            ProyectoDescripcion proyectoDescripcion = modelMapper.map(request.getDescripcion(), ProyectoDescripcion.class);
            proyectoDescripcion.setProyecto(proyecto);
            proyectoDescripcion = proyectoDescripcionRepository.save(proyectoDescripcion);
            request.getDescripcion().setIdProyectoDescripcion(proyectoDescripcion.getIdProyectoDescripcion());

            ProyectoImplementacion proyectoImplementacion = modelMapper.map(request.getImplementacion(), ProyectoImplementacion.class);
            if (request.getImplementacion().getEnfoques() != null && !request.getImplementacion().getEnfoques().isEmpty()) {
                proyectoImplementacion.setEnfoque(String.join(SEPARADOR, request.getImplementacion().getEnfoques()));
            }
            String rutaArchivo = uploadPath + RUTA_PDF_PROYECTO_IMPLEMENTACION + proyecto.getIdProyecto() + FORMATO_PDF_PROYECTO_IMPLEMENTACION;
            rutaArchivo = UploadUtil.saveFileBase64(rutaArchivo, request.getImplementacion().getArchivoBase64());
            proyectoImplementacion.setRutaArchivo(rutaArchivo);
            proyectoImplementacion.setProyecto(proyecto);
            proyectoImplementacion = proyectoImplementacionRepository.save(proyectoImplementacion);
            request.getImplementacion().setIdProyectoImplementacion(proyectoImplementacion.getIdProyectoImplementacion());

            request.setIdProyecto(proyecto.getIdProyecto());
        } else {

            Proyecto proyecto = proyectoRepository.findById(request.getIdProyecto())
                    .orElseThrow(() -> new ValidationException("El proyecto no existe"));

            if (proyecto.isEnviado()) {
                throw new ValidationException("El proyecto ya fue establecido como enviado, ya no puede modificarse");
            }

            if (!proyecto.getUsuarioCreacion().equals(authService.getIdUserSession())) {
                throw new ValidationException("El usuario no es lider de este proyecto");
            }

            ProyectoGrupo proyectoGrupo = proyecto.getProyectoGrupo();
            proyectoGrupo.setCategoria(request.getGrupo().getCategoria());
            proyectoGrupo.setJefe(request.getGrupo().getJefe());
            proyectoGrupo.setNombre(request.getGrupo().getNombre());
            proyectoGrupo.setSede(request.getGrupo().getSede());
            String rutaImagen = uploadPath + RUTA_IMAGENES_PROYECTO_GRUPO + proyecto.getIdProyecto() + FORMATO_IMAGEN_PROYECTO_GRUPO;
            rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getGrupo().getImagenBase64());
            proyectoGrupo.setRutaImagen(rutaImagen);
            proyectoGrupoRepository.save(proyectoGrupo);
            request.getGrupo().setIdProyectoGrupo(proyectoGrupo.getIdProyectoGrupo());

            List<ProyectoMiembroRequest> miembrosRequest = new ArrayList<>();
            for (ProyectoMiembroRequest proyectoMiembroRequest: request.getGrupo().getMiembros()) {
                boolean esNuevo = true;
                for (ProyectoMiembro proyectoMiembro: proyecto.getProyectoMiembros()) {
                    miembrosRequest.add(modelMapper.map(proyectoMiembro, ProyectoMiembroRequest.class));
                    if (proyectoMiembroRequest.getNumeroDocumento() != null && proyectoMiembroRequest.getNumeroDocumento().equals(proyectoMiembro.getNumeroDocumento())) {
                        esNuevo = false;
                        break;
                    }
                }
                if (esNuevo) {
                    validarMiembro(proyectoMiembroRequest);
                    ProyectoMiembro proyectoMiembro = modelMapper.map(proyectoMiembroRequest, ProyectoMiembro.class);
                    proyectoMiembro.setProyecto(proyecto);
                    proyectoMiembro = proyectoMiembroRepository.save(proyectoMiembro);
                    proyectoMiembroRequest.setIdProyectoMiembro(proyectoMiembro.getIdProyectoMiembro());
                    miembrosRequest.add(proyectoMiembroRequest);
                }
            }

            for (ProyectoMiembro proyectoMiembro: proyecto.getProyectoMiembros()) {
                boolean esAntiguo = true;
                for (ProyectoMiembroRequest proyectoMiembroRequest: miembrosRequest) {
                    if (proyectoMiembro.getNumeroDocumento().equals(proyectoMiembroRequest.getNumeroDocumento())) {
                        esAntiguo = false;
                    }
                }
                if (esAntiguo) {
                    proyectoMiembroRepository.delete(proyectoMiembro);
                }
            }

            ProyectoDescripcion proyectoDescripcion = proyecto.getProyectoDescripcion();
            proyectoDescripcion.setDescripcion(request.getDescripcion().getDescripcion());
            proyectoDescripcion.setContexto(request.getDescripcion().getContexto());
            proyectoDescripcion.setFecha(request.getDescripcion().getFecha());
            proyectoDescripcion.setIndicador(request.getDescripcion().getIndicador());
            proyectoDescripcion.setInnovacion(request.getDescripcion().getInnovacion());
            proyectoDescripcion.setMotivo(request.getDescripcion().getMotivo());
            proyectoDescripcionRepository.save(proyectoDescripcion);
            request.getDescripcion().setIdProyectoDescripcion(proyectoDescripcion.getIdProyectoDescripcion());

            ProyectoImplementacion proyectoImplementacion = proyecto.getProyectoImplementacion();
            if (request.getImplementacion().getEnfoques() != null && !request.getImplementacion().getEnfoques().isEmpty()) {
                proyectoImplementacion.setEnfoque(String.join(SEPARADOR, request.getImplementacion().getEnfoques()));
            }
            proyectoImplementacion.setBeneficio(request.getImplementacion().getBeneficio());
            proyectoImplementacion.setReplicable(request.getImplementacion().getReplicable());
            proyectoImplementacion.setReplicableFundamento(request.getImplementacion().getReplicableFundamento());
            proyectoImplementacion.setSostenible(request.getImplementacion().getSostenible());
            proyectoImplementacion.setSostenibleFundamento(request.getImplementacion().getSostenibleFundamento());
            proyectoImplementacion.setTecnologia(request.getImplementacion().getTecnologia());
            proyectoImplementacion.setTecnologiaFundamento(request.getImplementacion().getTecnologiaFundamento());
            proyectoImplementacion.setResultado(request.getImplementacion().getResultado());
            String rutaArchivo = uploadPath + RUTA_PDF_PROYECTO_IMPLEMENTACION + proyecto.getIdProyecto() + FORMATO_PDF_PROYECTO_IMPLEMENTACION;
            rutaArchivo = UploadUtil.saveFileBase64(rutaArchivo, request.getImplementacion().getArchivoBase64());
            proyectoImplementacion.setRutaArchivo(rutaArchivo);
            proyectoImplementacionRepository.save(proyectoImplementacion);
            request.getImplementacion().setIdProyectoImplementacion(proyectoImplementacion.getIdProyectoImplementacion());

            proyecto.setEnviado(request.isEnviado());
            proyecto.setUsuarioModificacion(authService.getIdUserSession());
            proyectoRepository.save(proyecto);
        }
        return request;
    }

    @Override
    public List<UsuarioDataResponse> listarUsuariosRed() {
        String red = "";
        Optional<RedPersonal> redPersonal = redPersonalRepository.findById(authService.getCodRedSession());
        if (redPersonal.isPresent()) {
            red = redPersonal.get().getDescripcion();
        }
        String finalRed = red;

        List<String> codigoRedes = Arrays.asList(authService.getCodRedSession());
        if (authService.getCodRedSession().equals(COD_RED_SEDE_CENTRAL_AFESSALUD) ||
                authService.getCodRedSession().equals(COD_RED_SEDE_CENTRAL_FONDOSALUD)) {
            codigoRedes = Arrays.asList(COD_RED_SEDE_CENTRAL_AFESSALUD, COD_RED_SEDE_CENTRAL_FONDOSALUD);
        }

        List<String> finalCodigoRedes = codigoRedes;
        return usuarioRepository.findAllByCodigoRedes(codigoRedes).stream()
                .map(u -> {
                    UsuarioDataResponse usuarioResponse = modelMapper.map(u, UsuarioDataResponse.class);
                    if (finalCodigoRedes.size() > 1) {
                        if (u.getCodigoRed().equals(COD_RED_SEDE_CENTRAL_AFESSALUD)) {
                            usuarioResponse.setRed(DESCRIPCION_RED_SEDE_CENTRAL_AFESSALUD);
                        } else {
                            usuarioResponse.setRed(DESCRIPCION_RED_SEDE_CENTRAL_FONDOSALUD);
                        }
                    } else {
                        usuarioResponse.setRed(finalRed);
                    }
                    return usuarioResponse;
                }).collect(Collectors.toList());
    }

    private void validarMiembro(ProyectoMiembroRequest proyectoMiembroRequest) {
        if (!proyectoMiembroRepository.findByNumeroDocumento(proyectoMiembroRequest.getNumeroDocumento()).isEmpty()) {
            throw new ValidationException("El integrante con DNI " + proyectoMiembroRequest.getNumeroDocumento() + ", ya está registrado en otro proyecto");
        }
        if (!proyectoRepository.findByUsuarioCreacion(proyectoMiembroRequest.getIdUsuario()).isEmpty()) {
            throw new ValidationException("El integrante con DNI " + proyectoMiembroRequest.getNumeroDocumento() + ", ya es lider en otro proyecto");
        }
    }

}