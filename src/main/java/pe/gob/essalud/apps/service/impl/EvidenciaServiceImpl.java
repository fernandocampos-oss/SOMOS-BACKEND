package pe.gob.essalud.apps.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.essalud.apps.common.constants.gestionrendimiento.EstadoEvidenciaConstant;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadExistRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EvidenciaSustentoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorExistRequestDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EvidenciaRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.IndicadorRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.EvidenciaService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvidenciaServiceImpl implements EvidenciaService {

    private static final String RUTA_IMAGENES_GESTION_RENDIMIENTO = "/imagenes/gestion-rendimiento/";
    private static final String RUTA_PDF_GESTION_RENDIMIENTO = "/pdf/gestion-rendimiento/";
    private static final String FORMATO_IMAGEN_EVIDENCIA = ".png";
    private static final String FORMATO_PDF_EVIDENCIA = ".pdf";

    private final EvidenciaRepository evidenciaRepository;
    private final AuthService authService;
    private final IndicadorRepository indicadorRepository;

    @Value("${upload-path}")
    private String uploadPath;

    @Transactional
    @Override
    public void registrarEvidenciaExistIndicador(IndicadorExistRequestDto dto) {
        if (!dto.getListEvidencia().isEmpty()) {
            for (Evidencia e : dto.getListEvidencia()) {
                e.setIndicador(dto.getIndicador());
                e.setUsuarioCreacion(authService.getIdUserSession());

                EstadoEvidencia model = new EstadoEvidencia();
                model.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
                e.setEstadoEvidencia(model);

                e.setEstado(true);
                evidenciaRepository.save(e);
            }
        }
    }

    @Transactional
    @Override
    public void registrarIndicadorExistPrioridad(PrioridadExistRequestDto dto) {
        LocalDate fechaActualTmp = LocalDate.now();
        int anioRegistro = fechaActualTmp.getYear();

        Indicador model = dto.getIndicador();
        model.setAnio(anioRegistro);
        model.setEstado(true);
        model.setUsuarioCreacion(authService.getIdUserSession());
        model.setVotante(dto.getVotante());
        model.setPrioridad(dto.getPrioridad());
        Indicador indicadorGuardado = indicadorRepository.save(model);
        log.info("indicadorGuardado [{}]", indicadorGuardado.getIdIndicador());

        if (!dto.getListEvidencia().isEmpty()) {
            for (Evidencia e : dto.getListEvidencia()) {
                e.setIndicador(indicadorGuardado);
                e.setUsuarioCreacion(authService.getIdUserSession());

                EstadoEvidencia modelEstado = new EstadoEvidencia();
                modelEstado.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
                e.setEstadoEvidencia(modelEstado);

                e.setEstado(true);
                evidenciaRepository.save(e);
            }
        }
    }

    @Override
    public int actualizarTareaAdministrador(String nombre, String plazo, Number idEvidencia) {
        return evidenciaRepository.actualizarTareaAdministrador(nombre, plazo, authService.getIdUserSession(), LocalDateTime.now(ZoneId.of("America/Lima")), idEvidencia);
    }

    @Transactional
    @Override
    public long crearSustentoEvidencia(EvidenciaSustentoRequestDto request) {
        if (request.getExtension().equals("pdf")) {
            String rutaFile = uploadPath + RUTA_PDF_GESTION_RENDIMIENTO + request.getIdEvidencia() + FORMATO_PDF_EVIDENCIA;
            rutaFile = UploadUtil.saveFileBase64(rutaFile, request.getFileBase64());
            evidenciaRepository.crearEvidencia(request.getSustentoDescripcion(), rutaFile, request.getExtension(), LocalDateTime.now(ZoneId.of("America/Lima")), request.getIdEvidencia());
        } else {
            String rutaFile = uploadPath + RUTA_IMAGENES_GESTION_RENDIMIENTO + request.getIdEvidencia() + FORMATO_IMAGEN_EVIDENCIA;
            rutaFile = UploadUtil.saveFileBase64(rutaFile, request.getFileBase64());
            evidenciaRepository.crearEvidencia(request.getSustentoDescripcion(), rutaFile, "png", LocalDateTime.now(ZoneId.of("America/Lima")), request.getIdEvidencia());
        }
        return request.getIdEvidencia();
    }

    @Override
    public EvidenciaResponseDto getEvidenciaByTarea(Integer idEvidencia) {
        Optional<Evidencia> tarea = evidenciaRepository.findById(idEvidencia);
        log.info("idEvidencia [{}]", tarea.get().getIdEvidencia());
        EvidenciaResponseDto dto = new EvidenciaResponseDto();
        if (tarea.isPresent()) {
            String baseImagen = UploadUtil.getFileBase64(tarea.get().getSustentoRutaFile());

            dto.setEvidenciaDescripcion(tarea.get().getSustentoDescripcion());
            dto.setEvidenciaFechaRegistro(tarea.get().getSustentoFechaRegistro());
            dto.setFileBase64(baseImagen);
            dto.setExtension(tarea.get().getSustentoExtensionFile());
        }
        return dto;
    }

    @Override
    public List<Evidencia> listEvidenciaByIdIndicador(int idIndicador) {
        return evidenciaRepository.listEvidenciaByIdIndicador(idIndicador);
    }

//    @Override
//    public int aprobarIndicador(Number estado, Number idIndicadorUsuario) {
//        return requerimientoUsuarioRepository.aprobarIndicador(estado, idIndicadorUsuario);
//    }
//
//    @Override
//    public int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario) {
//        return requerimientoUsuarioRepository.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
//    }

}
