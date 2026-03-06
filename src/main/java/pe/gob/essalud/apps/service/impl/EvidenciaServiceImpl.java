package pe.gob.essalud.apps.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.essalud.apps.common.constants.gestionrendimiento.EstadoEvidenciaConstant;
import pe.gob.essalud.apps.common.util.DateUtil;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvidenciaResponseDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EvidenciaRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.IndicadorRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.EvidenciaService;
import pe.gob.essalud.apps.service.gdr.SentidoIndicadorService;
import pe.gob.essalud.apps.service.gdr.EvidenciaTipoService;
import pe.gob.essalud.apps.service.gdr.ComentarioEstadoService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvidenciaServiceImpl implements EvidenciaService {

    private static final String RUTA_IMAGENES_GESTION_RENDIMIENTO = "/imagenes/gestion-rendimiento/";
    private static final String RUTA_PDF_GESTION_RENDIMIENTO = "/pdf/gestion-rendimiento/";
    private static final String FORMATO_IMAGEN_EVIDENCIA = ".png";
    private static final String FORMATO_PDF_EVIDENCIA = ".pdf";

    private final EvidenciaRepository evidenciaRepository;
    private final EvidenciaTipoService evidenciaTipoService;
    private final ComentarioEstadoService comentarioEstadoService;
    private final AuthService authService;
    private final IndicadorRepository indicadorRepository;
    private final SentidoIndicadorService sentidoIndicadorService;

    @Value("${upload-path}")
    private String uploadPath;

    @Transactional
    @Override
    public void registrarEvidenciaExistIndicador(IndicadorExistRequestDto dto) {
        if (!dto.getListEvidencia().isEmpty()) {
            int orden = 1;
            int totalEvidencias = dto.getListEvidencia().size();
            
            for (int i = 0; i < totalEvidencias; i++) {
                Evidencia e = dto.getListEvidencia().get(i);
                e.setIndicador(dto.getIndicador());
                e.setUsuarioCreacion(authService.getIdUserSession());

                EstadoEvidencia model = new EstadoEvidencia();
                model.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
                e.setEstadoEvidencia(model);

                e.setEstado(true);
                Evidencia evidenciaGuardada = evidenciaRepository.save(e);
                
                // Guardar tipo y orden en BD local
                try {
                    String tipo = (i == totalEvidencias - 1) ? "final" : "inicial";
                    evidenciaTipoService.guardarOActualizar(
                        evidenciaGuardada.getIdEvidencia().longValue(),
                        dto.getIndicador().getIdIndicador().longValue(),
                        tipo,
                        orden++
                    );
                    log.info("Tipo de evidencia guardado: ID={}, Tipo={}, Orden={}", 
                        evidenciaGuardada.getIdEvidencia(), tipo, orden - 1);
                } catch (Exception ex) {
                    log.error("Error al guardar tipo de evidencia: {}", ex.getMessage());
                }
            }
        }
    }

    @Transactional
    @Override
    public Integer registrarIndicadorExistPrioridad(PrioridadExistRequestDto dto) {
        log.info("=== INICIO registrarIndicadorExistPrioridad ===");
        log.info("Datos recibidos: sentidoIndicador={}, fechaPlazoFinal={}", 
            dto.getSentidoIndicador(), dto.getFechaPlazoFinal());
        log.info("Lista evidencias: {}", dto.getListEvidencia() != null ? dto.getListEvidencia().size() : "NULL");
        
        Indicador model = dto.getIndicador();
        model.setAnio(DateUtil.getYearCurrent());
        model.setEstado(true);
        model.setUsuarioCreacion(authService.getIdUserSession());
        model.setVotante(dto.getVotante());
        model.setPrioridad(dto.getPrioridad());
        model.setCodRed(authService.getCodRedSession());
        model.setCodUnidad(authService.getCodUnidadSession());
        Indicador indicadorGuardado = indicadorRepository.save(model);
        log.info("Indicador guardado: ID={}", indicadorGuardado.getIdIndicador());

        // Procesar evidencias si existen
        if (dto.getListEvidencia() != null && !dto.getListEvidencia().isEmpty()) {
            log.info("Procesando {} evidencias iniciales...", dto.getListEvidencia().size());
            int orden = 1;
            
            for (int i = 0; i < dto.getListEvidencia().size(); i++) {
                Evidencia e = dto.getListEvidencia().get(i);
                e.setIndicador(indicadorGuardado);
                e.setUsuarioCreacion(authService.getIdUserSession());

                EstadoEvidencia modelEstado = new EstadoEvidencia();
                modelEstado.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
                e.setEstadoEvidencia(modelEstado);

                e.setEstado(true);
                Evidencia evidenciaGuardada = evidenciaRepository.save(e);
                log.info("Evidencia inicial guardada: ID={}", evidenciaGuardada.getIdEvidencia());
                
                // Guardar tipo y orden en BD local - todas son iniciales
                try {
                    log.info("Guardando tipo evidencia: idEvidencia={}, idIndicador={}, tipo=inicial, orden={}", 
                        evidenciaGuardada.getIdEvidencia(), indicadorGuardado.getIdIndicador(), orden);
                    evidenciaTipoService.guardarOActualizar(
                        evidenciaGuardada.getIdEvidencia().longValue(),
                        indicadorGuardado.getIdIndicador().longValue(),
                        "inicial",
                        orden++
                    );
                    log.info("Tipo de evidencia guardado exitosamente");
                } catch (Exception ex) {
                    log.error("ERROR al guardar tipo de evidencia: {} - {}", ex.getClass().getName(), ex.getMessage(), ex);
                }
            }
        } else {
            log.info("No hay evidencias iniciales para procesar");
        }
        
        // SIEMPRE crear la evidencia final con descripcion 'SUSTENTO FINAL'
        log.info("Creando evidencia final obligatoria...");
        Evidencia evidenciaFinal = new Evidencia();
        evidenciaFinal.setDescripcion("SUSTENTO FINAL");
        evidenciaFinal.setIndicador(indicadorGuardado);
        evidenciaFinal.setUsuarioCreacion(authService.getIdUserSession());
        
        EstadoEvidencia estadoFinal = new EstadoEvidencia();
        estadoFinal.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
        evidenciaFinal.setEstadoEvidencia(estadoFinal);
        evidenciaFinal.setEstado(true);
        
        // Si hay fecha de plazo final, asignarla
        if (dto.getFechaPlazoFinal() != null && !dto.getFechaPlazoFinal().isEmpty()) {
            try {
                LocalDateTime fechaPlazo = LocalDateTime.parse(dto.getFechaPlazoFinal());
                evidenciaFinal.setPlazo(fechaPlazo);
            } catch (Exception ex) {
                log.warn("No se pudo parsear fechaPlazoFinal: {}", dto.getFechaPlazoFinal());
            }
        }
        
        Evidencia evidenciaFinalGuardada = evidenciaRepository.save(evidenciaFinal);
        log.info("Evidencia FINAL guardada: ID={}", evidenciaFinalGuardada.getIdEvidencia());
        
        // Guardar tipo 'final' en BD local
        try {
            int ordenFinal = (dto.getListEvidencia() != null ? dto.getListEvidencia().size() : 0) + 1;
            evidenciaTipoService.guardarOActualizar(
                evidenciaFinalGuardada.getIdEvidencia().longValue(),
                indicadorGuardado.getIdIndicador().longValue(),
                "final",
                ordenFinal
            );
        } catch (Exception ex) {
            log.error("ERROR al guardar tipo de evidencia final: {} - {}", ex.getClass().getName(), ex.getMessage(), ex);
        }
        
        log.info("=== FIN registrarIndicadorExistPrioridad, retornando ID={} ===", indicadorGuardado.getIdIndicador());
        return indicadorGuardado.getIdIndicador();
    }
    

    @Transactional
    @Override
    public long crearSustentoEvidencia(EvidenciaSustentoRequestDto request) {
        if (request.getExtension().equals("pdf")) {
            String rutaFile = uploadPath + RUTA_PDF_GESTION_RENDIMIENTO + request.getIdEvidencia() + FORMATO_PDF_EVIDENCIA;
            rutaFile = UploadUtil.saveFileBase64(rutaFile, request.getFileBase64());
            evidenciaRepository.crearEvidencia(request.getSustentoDescripcion(), rutaFile, request.getExtension(), LocalDateTime.now(ZoneId.of("America/Lima")), request.getCalificacion(), request.getIdEvidencia());
        } else {
            String rutaFile = uploadPath + RUTA_IMAGENES_GESTION_RENDIMIENTO + request.getIdEvidencia() + FORMATO_IMAGEN_EVIDENCIA;
            rutaFile = UploadUtil.saveFileBase64(rutaFile, request.getFileBase64());
            evidenciaRepository.crearEvidencia(request.getSustentoDescripcion(), rutaFile, "png", LocalDateTime.now(ZoneId.of("America/Lima")), request.getCalificacion(), request.getIdEvidencia());
        }
        return request.getIdEvidencia();
    }

    @Override
    public EvidenciaResponseDto getEvidenciaById(Integer idEvidencia) {
        Optional<Evidencia> tarea = evidenciaRepository.findById(idEvidencia);

        EvidenciaResponseDto dto = new EvidenciaResponseDto();
        if (tarea.isPresent()) {
            String baseImagen = UploadUtil.getFileBase64(tarea.get().getSustentoRutaFile());

            dto.setEvidenciaDescripcion(tarea.get().getSustentoDescripcion());
            dto.setEvidenciaFechaRegistro(tarea.get().getSustentoFechaRegistro());
            dto.setFileBase64(baseImagen);
            dto.setExtension(tarea.get().getSustentoExtensionFile());
            dto.setCalificacion(tarea.get().getCalificacion());
            dto.setComentario(tarea.get().getComentario());
        }
        return dto;
    }

//    @Override
//    public List<Evidencia> listEvidenciaByIdIndicador(int idIndicador) {
//        return evidenciaRepository.listEvidenciaByIdIndicador(idIndicador);
//    }

    @Override
    public void modificarEvidencia(int id, UpdateEvidenciaDto request) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ValidationException("La evidencia no se encuentra"));

        evidencia.setDescripcion(request.getDescripcion());
        evidencia.setPlazo(request.getPlazo());
        evidencia.setUsuarioModificacion(authService.getIdUserSession());
        evidenciaRepository.save(evidencia);
    }

    @Override
    public void aprobarEvidencia(ApruebaEvidenciaRequestDto request) {
        Evidencia evidencia = evidenciaRepository.findById(request.getIdEvidencia())
                .orElseThrow(() -> new ValidationException("La evidencia no se encuentra"));
        evidencia.setComentario(request.getComentario());
        evidencia.setCalificacion(request.getCalificacion());
        evidenciaRepository.save(evidencia);
    }

    @Override
    public void eliminarEvidencia(int id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ValidationException("La evidencia no se encuentra"));
        if (StringUtils.isNotBlank(evidencia.getSustentoRutaFile())) {
            throw new ValidationException("La evidencia tiene sustento registrado");
        }
        evidencia.setEstado(false);
        evidenciaRepository.save(evidencia);
        
        // Eliminar de BD local
        try {
            evidenciaTipoService.eliminarPorIdEvidencia(Long.valueOf(id));
            comentarioEstadoService.eliminarPorIdEvidencia(Long.valueOf(id));
            
            // Reordenar evidencias del mismo indicador
            if (evidencia.getIndicador() != null) {
                evidenciaTipoService.reordenarEvidencias(Long.valueOf(evidencia.getIndicador().getIdIndicador()));
            }
            
            log.info("Evidencia eliminada de BD local: ID={}", id);
        } catch (Exception e) {
            log.error("Error al eliminar evidencia de BD local: {}", e.getMessage());
        }
    }

    @Override
    public void eliminarSustento(int id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ValidationException("La evidencia no se encuentra"));
        evidencia.setSustentoDescripcion(null);
        evidencia.setSustentoExtensionFile(null);
        evidencia.setSustentoFechaRegistro(null);
        evidencia.setSustentoExtensionFile(null);
        evidencia.setSustentoRutaFile(null);
        evidenciaRepository.save(evidencia);
    }

}
