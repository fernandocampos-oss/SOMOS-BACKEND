package pe.gob.essalud.apps.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.essalud.apps.common.constants.gestionrendimiento.EstadoTareaConstant;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.TareaRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.TareaRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.TareaService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareaServiceImpl implements TareaService {

    private static final String RUTA_IMAGENES_GESTION_RENDIMIENTO = "/imagenes/gestion-rendimiento/";
    private static final String RUTA_PDF_GESTION_RENDIMIENTO = "/pdf/gestion-rendimiento/";
    private static final String FORMATO_IMAGEN_EVIDENCIA = ".png";
    private static final String FORMATO_PDF_EVIDENCIA = ".pdf";

    private final TareaRepository tareaRepository;
    private final AuthService authService;

    @Value("${upload-path}")
    private String uploadPath;

    @Transactional
    @Override
    public Integer registrarTarea(TareaRequestDto dto) {
        if (!dto.getListTarea().isEmpty()) {
            for (Tarea i : dto.getListTarea()) {
                i.setIndicador(dto.getIndicador());
                i.setUsuarioCreacion(authService.getIdUserSession());

                EstadoTarea model = new EstadoTarea();
                model.setIdEstadoTarea(EstadoTareaConstant.REGISTRADO);
                i.setEstadoTarea(model);

                i.setEstado(true);
                tareaRepository.save(i);
            }
        }
        return dto.getIndicador().getIdIndicador();
    }

    @Override
    public int actualizarTareaAdministrador(String nombre, String plazo, Number idTarea) {
        return tareaRepository.actualizarTareaAdministrador(nombre, plazo, authService.getIdUserSession(), LocalDateTime.now(ZoneId.of("America/Lima")), idTarea);
    }

    @Transactional
    @Override
    public long crearEvidenciaTarea(EvidenciaRequestDto request) {
        if (request.getExtension().equals("pdf")) {
            String rutaFile = uploadPath + RUTA_PDF_GESTION_RENDIMIENTO + request.getIdTarea() + FORMATO_PDF_EVIDENCIA;
            rutaFile = UploadUtil.saveFileBase64(rutaFile, request.getFileBase64());
            tareaRepository.crearEvidencia(request.getEvidenciaDescripcion(), rutaFile, request.getExtension(), LocalDateTime.now(ZoneId.of("America/Lima")), request.getIdTarea());
        } else {
            String rutaFile = uploadPath + RUTA_IMAGENES_GESTION_RENDIMIENTO + request.getIdTarea() + FORMATO_IMAGEN_EVIDENCIA;
            rutaFile = UploadUtil.saveFileBase64(rutaFile, request.getFileBase64());
            tareaRepository.crearEvidencia(request.getEvidenciaDescripcion(), rutaFile, "png", LocalDateTime.now(ZoneId.of("America/Lima")), request.getIdTarea());
        }
        return request.getIdTarea();
    }

    @Override
    public EvidenciaResponseDto getEvidenciaByTarea(Integer idTarea) {
        Optional<Tarea> tarea = tareaRepository.findById(idTarea);
        log.info("idTarea [{}]", tarea.get().getIdTarea());
        EvidenciaResponseDto dto = new EvidenciaResponseDto();
        if (tarea.isPresent()) {
            String baseImagen = UploadUtil.getFileBase64(tarea.get().getEvidenciaRutaFile());

            dto.setEvidenciaDescripcion(tarea.get().getEvidenciaDescripcion());
            dto.setEvidenciaFechaRegistro(tarea.get().getEvidenciaFechaRegistro());
            dto.setFileBase64(baseImagen);
            dto.setExtension(tarea.get().getEvidenciaExtensionFile());
        }
        return dto;
    }

    @Override
    public List<Tarea> getTareasByIdIndicador(int idIndicador) {
        return tareaRepository.getTareasByIdIndicador(idIndicador);
    }

    ////    @Override
////    public int aprobarIndicador(Number estado, Number idIndicadorUsuario) {
////        return requerimientoUsuarioRepository.aprobarIndicador(estado, idIndicadorUsuario);
////    }
//
////    @Override
////    public int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario) {
////        return requerimientoUsuarioRepository.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
////    }

}
