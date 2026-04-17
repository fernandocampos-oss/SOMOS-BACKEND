package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.common.constants.gestionrendimiento.EstadoEvidenciaConstant;
import pe.gob.essalud.apps.common.util.DateUtil;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.*;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.GdrParametroRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.IndicadorService;
import pe.gob.essalud.apps.service.gdr.SentidoIndicadorService;
import pe.gob.essalud.apps.service.gdr.EvidenciaTipoService;
import pe.gob.essalud.apps.repository.gdr.SegmentoGdrRepository;
import pe.gob.essalud.apps.model.gdr.SegmentoGdr;
import pe.gob.essalud.apps.repository.gdr.ReunionEstablecimientoMetasRepository;
import pe.gob.essalud.apps.model.gdr.ReunionEstablecimientoMetas;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicadorServiceImpl implements IndicadorService {

    private final IndicadorRepository indicadorRepository;
    private final AuthService authService;
    private final TipoValorMetaRepository tipoValorMetaRepository;
    private final PrioridadRepository prioridadRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final EquipoRepository equipoRepository;
    private final GdrParametroRepository gdrParametroRepository;
    private final SentidoIndicadorService sentidoIndicadorService;
    private final EvidenciaTipoService evidenciaTipoService;
    private final SegmentoGdrRepository segmentoGdrRepository;
    private final ReunionEstablecimientoMetasRepository reunionMetasRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtener segmento desde la tabla segmento_gdr, si no existe usa fallback basado en idSegmento
     */
    private String obtenerSegmento(String numeroDocumento, Integer idSegmento) {
        if (numeroDocumento != null) {
            Optional<SegmentoGdr> segmentoGdr = segmentoGdrRepository.findByNumeroDocumento(numeroDocumento);
            if (segmentoGdr.isPresent()) {
                return segmentoGdr.get().getSegmento();
            }
        }
        // Fallback al valor anterior
        if (idSegmento != null) {
            if (idSegmento == 1) return "DIRECTIVO";
            if (idSegmento == 3) return "EJECUTOR";
        }
        return "";
    }

    /**
     * Obtener datos de reunión establecimiento de metas para el Excel
     */
    private void llenarDatosReunion(ExcelTrabajadorDto dto, Long idVotanteEvaluado, Long idVotanteEvaluador) {
        try {
            String periodo = String.valueOf(DateUtil.getYearCurrent());
            log.info("llenarDatosReunion: evaluado={}, evaluador={}, periodo={}", idVotanteEvaluado, idVotanteEvaluador, periodo);
            
            Optional<ReunionEstablecimientoMetas> reunionOpt = reunionMetasRepository
                .findByIdVotanteEvaluadoAndIdVotanteEvaluadorAndPeriodo(idVotanteEvaluado, idVotanteEvaluador, periodo);
            
            log.info("llenarDatosReunion: reunionOpt.isPresent={}", reunionOpt.isPresent());
            
            if (reunionOpt.isPresent()) {
                ReunionEstablecimientoMetas reunion = reunionOpt.get();
                log.info("llenarDatosReunion: asistio={}, fechaReunion={}", reunion.getAsistio(), reunion.getFechaReunion());
                // Asistió: S=Sí, cualquier otro valor (N, -)=No
                if ("S".equals(reunion.getAsistio())) {
                    dto.setReunionAsistio("Sí");
                    // Fecha de reunión solo si asistió
                    if (reunion.getFechaReunion() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        dto.setReunionFecha(reunion.getFechaReunion().format(formatter));
                    } else {
                        dto.setReunionFecha("-");
                    }
                } else {
                    dto.setReunionAsistio("No");
                    dto.setReunionFecha("-");
                }
            } else {
                // Sin registro = No asistió
                dto.setReunionAsistio("No");
                dto.setReunionFecha("-");
            }
            log.info("llenarDatosReunion: reunionAsistio={}, reunionFecha={}", dto.getReunionAsistio(), dto.getReunionFecha());
        } catch (Exception e) {
            log.error("Error al llenar datos de reunión: {}", e.getMessage(), e);
            dto.setReunionAsistio("No");
            dto.setReunionFecha("-");
        }
    }

    @Override
    @Transactional
    public Integer registrarIndicador(IndicadorRequestDto requestDto) {
        log.info("=== INICIO registrarIndicador ===");
        log.info("Datos recibidos: sentidoIndicador={}, fechaPlazoFinal={}, prioridadNombre={}", 
            requestDto.getSentidoIndicador(), requestDto.getFechaPlazoFinal(), requestDto.getPrioridadNombre());
        
        Prioridad prioridad = new Prioridad();
        prioridad.setAnio(DateUtil.getYearCurrent());
        prioridad.setActividad(requestDto.getActividad());
        
        String flDesPrioridad = requestDto.getIndicador().getFlDesPrioridad();
        log.info("flDesPrioridad: {}", flDesPrioridad);
        
        // Usar prioridadNombre del DTO si existe, sino usar desPrioridad del indicador
        if("1".equals(flDesPrioridad)) {
        	log.info("Reemplaza valor con desPrioridad: {}", requestDto.getIndicador().getDesPrioridad());
        	prioridad.setDescripcion(requestDto.getIndicador().getDesPrioridad());
        } else if (requestDto.getPrioridadNombre() != null && !requestDto.getPrioridadNombre().isEmpty()) {
            log.info("Usando prioridadNombre del DTO: {}", requestDto.getPrioridadNombre());
            prioridad.setDescripcion(requestDto.getPrioridadNombre());
        }
        Prioridad prioridadGuardado = prioridadRepository.save(prioridad);
        log.info("Prioridad guardada: ID={}", prioridadGuardado.getIdPrioridad());

        Indicador model = requestDto.getIndicador();
        model.setAnio(DateUtil.getYearCurrent());
        model.setEstado(true);
        model.setUsuarioCreacion(authService.getIdUserSession());
        model.setVotante(requestDto.getVotante());
        model.setPrioridad(prioridadGuardado);
        model.setCodRed(authService.getCodRedSession());
        model.setCodUnidad(authService.getCodUnidadSession());
        
        Indicador indicadorGuardado = indicadorRepository.save(model);
        log.info("Indicador guardado: ID={}", indicadorGuardado.getIdIndicador());

        // Procesar evidencias si existen
        if (requestDto.getListEvidencia() != null && !requestDto.getListEvidencia().isEmpty()) {
            log.info("Procesando {} evidencias...", requestDto.getListEvidencia().size());
            int orden = 1;
            for (Evidencia i : requestDto.getListEvidencia()) {
                i.setIndicador(indicadorGuardado);
                i.setUsuarioCreacion(authService.getIdUserSession());

                EstadoEvidencia estadoEvidencia = new EstadoEvidencia();
                estadoEvidencia.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
                i.setEstadoEvidencia(estadoEvidencia);

                i.setEstado(true);
                Evidencia evidenciaGuardada = evidenciaRepository.save(i);
                log.info("Evidencia inicial guardada: ID={}", evidenciaGuardada.getIdEvidencia());
                
                // Guardar evidencia_tipo en BD local
                try {
                    evidenciaTipoService.guardarOActualizar(
                        evidenciaGuardada.getIdEvidencia().longValue(),
                        indicadorGuardado.getIdIndicador().longValue(),
                        "inicial",
                        orden++
                    );
                    log.info("Evidencia_tipo guardada: idEvidencia={}, orden={}", 
                        evidenciaGuardada.getIdEvidencia(), orden-1);
                } catch (Exception e) {
                    log.error("Error al guardar evidencia_tipo: {}", e.getMessage());
                }
            }
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
        if (requestDto.getFechaPlazoFinal() != null && !requestDto.getFechaPlazoFinal().isEmpty()) {
            try {
                LocalDateTime fechaPlazo = LocalDateTime.parse(requestDto.getFechaPlazoFinal());
                evidenciaFinal.setPlazo(fechaPlazo);
            } catch (Exception ex) {
                log.warn("No se pudo parsear fechaPlazoFinal: {}", requestDto.getFechaPlazoFinal());
            }
        }
        
        Evidencia evidenciaFinalGuardada = evidenciaRepository.save(evidenciaFinal);
        log.info("Evidencia FINAL guardada: ID={}", evidenciaFinalGuardada.getIdEvidencia());
        
        // Guardar tipo 'final' en BD local
        try {
            int ordenFinal = (requestDto.getListEvidencia() != null ? requestDto.getListEvidencia().size() : 0) + 1;
            evidenciaTipoService.guardarOActualizar(
                evidenciaFinalGuardada.getIdEvidencia().longValue(),
                indicadorGuardado.getIdIndicador().longValue(),
                "final",
                ordenFinal
            );
        } catch (Exception ex) {
            log.error("ERROR al guardar tipo de evidencia final: {} - {}", ex.getClass().getName(), ex.getMessage());
        }
        
        log.info("=== FIN registrarIndicador, retornando ID={} ===", indicadorGuardado.getIdIndicador());
        return indicadorGuardado.getIdIndicador();
    }

    @Override
    public List<PendienteDto> listPendientesTrabajadorByUser() {
        Votante votante = null;
        Optional<Usuario> usuarioOpt = usuarioRepository.findById((long) authService.getIdUserSession());
        if (usuarioOpt.isPresent()) {
            votante = equipoRepository.getVotanteByNumeroDocumento(usuarioOpt.get().getNumeroDocumento());
        }
        if (votante == null) {
            votante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
        }
        if (votante == null) {
            log.warn("No se encontró votante para id_usuario={}", authService.getIdUserSession());
            return new ArrayList<>();
        }

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), votante.getIdVotante());
        String aux = "";
        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            if(p.getDescripcion() == null) {
            	modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());
            }else {
            	modelPrioridadDto.setPrioridadNombre(p.getDescripcion());
            }

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(votante.getIdVotante(), p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());

                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());

                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        return listPrioridadDto;
    }

    @Override
    public List<PendienteDto> listPendientesTrabajadorByVotanteAdmin(int idVotante) {
        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), idVotante);

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getDescripcion());
            modelPrioridadDto.setIdActividad(p.getActividad() != null ? p.getActividad().getIdActividad() : null);
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            int porcentajeTotal = 0;

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(idVotante, p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setIdTipoValorMeta(i.getTipoValorMeta() != null ? i.getTipoValorMeta().getIdTipoValorMeta() : null);
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta() != null ? i.getTipoValorMeta().getCodigo() : null);
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());
                int numero = 0;
                numero = i.getPeso();
                porcentajeTotal += numero;

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    log.info("[{}]", t.getDescripcion());
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());

                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());

                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                modelPrioridadDto.setPeso(porcentajeTotal);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        return listPrioridadDto;
    }

    @Override
    public List<TipoValorMeta> getAllTipoValorMeta() {
        return tipoValorMetaRepository.findAllByEstado(true);
    }

    @Override
    public ExcelTrabajadorDto generarExcelTrabajador() {

        ExcelTrabajadorDto mainDto = new ExcelTrabajadorDto();

        EvaluadorResponseDto trabajadorUsuario = prioridadRepository.findUsuarioById(authService.getIdUserSession());
        Votante votanteTrabajador = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
        mainDto.setEvaluadoNombreCompleto(votanteTrabajador.getApellidos() + " " + votanteTrabajador.getNombres());
        mainDto.setEvaluadoPuesto(trabajadorUsuario.getPuesto());
        UnidadOrganizativa unidadtrabajador = prioridadRepository.getUnidadByCod(trabajadorUsuario.getUnidad());
        mainDto.setEvaluadoCodUnidad(unidadtrabajador.getDescripcion());
        mainDto.setEvaluadoNumeroDocumento(votanteTrabajador.getNumeroDocumento());
        mainDto.setEvaluadoSegmento(obtenerSegmento(votanteTrabajador.getNumeroDocumento(), votanteTrabajador.getIdSegmento()));
        Equipo JefeEquipo = equipoRepository.getJefeByIdIntegrante(votanteTrabajador.getIdVotante());
        EvaluadorResponseDto jefe = prioridadRepository.findUsuarioById(JefeEquipo.getJefe().getIdUsuario());
        UnidadOrganizativa unidadJefe = prioridadRepository.getUnidadByCod(jefe.getUnidad());
        mainDto.setEvaluadorCodUnidad(unidadJefe.getDescripcion());
        mainDto.setEvaluadorNombreCompleto(JefeEquipo.getJefe().getApellidos() + " " + JefeEquipo.getJefe().getNombres());
        mainDto.setEvaluadorPuesto(jefe.getPuesto());
        mainDto.setEvaluadorSegmento(obtenerSegmento(JefeEquipo.getJefe().getNumeroDocumento(), JefeEquipo.getJefe().getIdSegmento()));
        mainDto.setEvaluadorNumeroDocumento(jefe.getNumeroDocumento());

        // Datos de reunión establecimiento de metas
        llenarDatosReunion(mainDto, (long) votanteTrabajador.getIdVotante(), (long) JefeEquipo.getJefe().getIdVotante());

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), votanteTrabajador.getIdVotante());

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getDescripcion());

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(votanteTrabajador.getIdVotante(), p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());
                    modelEvidenciaDto.setComentario(t.getComentario());
                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());

                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        mainDto.setListPrioridad(listPrioridadDto);
        return mainDto;
    }

    @Override
    public ExcelTrabajadorDto generarExcelTrabajadorByVotanteAdmin(int idVotante) {
        ExcelTrabajadorDto mainDto = new ExcelTrabajadorDto();

        Votante votanteTrabajador = equipoRepository.getVotanteByIdVotante(idVotante);
        EvaluadorResponseDto trabajadorUsuario = prioridadRepository.findUsuarioById(votanteTrabajador.getIdUsuario());
        mainDto.setEvaluadoNombreCompleto(votanteTrabajador.getApellidos() + " " + votanteTrabajador.getNombres());
        mainDto.setEvaluadoPuesto(trabajadorUsuario.getPuesto());
        UnidadOrganizativa unidadtrabajador = prioridadRepository.getUnidadByCod(trabajadorUsuario.getUnidad());
        mainDto.setEvaluadoCodUnidad(unidadtrabajador.getDescripcion());
        mainDto.setEvaluadoNumeroDocumento(votanteTrabajador.getNumeroDocumento());
        mainDto.setEvaluadoSegmento(obtenerSegmento(votanteTrabajador.getNumeroDocumento(), votanteTrabajador.getIdSegmento()));
        Equipo JefeEquipo = equipoRepository.getJefeByIdIntegrante(votanteTrabajador.getIdVotante());
        EvaluadorResponseDto jefe = prioridadRepository.findUsuarioById(JefeEquipo.getJefe().getIdUsuario());
        UnidadOrganizativa unidadJefe = prioridadRepository.getUnidadByCod(jefe.getUnidad());
        mainDto.setEvaluadorCodUnidad(unidadJefe.getDescripcion());
        mainDto.setEvaluadorNombreCompleto(JefeEquipo.getJefe().getApellidos() + " " + JefeEquipo.getJefe().getNombres());
        mainDto.setEvaluadorPuesto(jefe.getPuesto());
        mainDto.setEvaluadorSegmento(obtenerSegmento(JefeEquipo.getJefe().getNumeroDocumento(), JefeEquipo.getJefe().getIdSegmento()));
        mainDto.setEvaluadorNumeroDocumento(jefe.getNumeroDocumento());

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), votanteTrabajador.getIdVotante());

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getDescripcion());

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(votanteTrabajador.getIdVotante(), p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                modelIndicadorDto.setValorMeta(i.getValorMeta());
                modelIndicadorDto.setPeso(i.getPeso());

                List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                List<PendienteEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                for (Evidencia t : listEvidencia) {
                    PendienteEvidenciaDto modelEvidenciaDto = new PendienteEvidenciaDto();
                    modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                    modelEvidenciaDto.setDescripcion(t.getDescripcion());
                    modelEvidenciaDto.setPlazo(t.getPlazo());
                    modelEvidenciaDto.setComentario(t.getComentario());
                    modelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                    modelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                    modelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                    modelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());

                    listEvidenciaDto.add(modelEvidenciaDto);
                }
                modelIndicadorDto.setListEvidencia(listEvidenciaDto);
                listIndicadorDto.add(modelIndicadorDto);
            }
            modelPrioridadDto.setListIndicador(listIndicadorDto);
            listPrioridadDto.add(modelPrioridadDto);
        }
        mainDto.setListPrioridad(listPrioridadDto);
        
        // Llenar datos de reunión establecimiento de metas
        llenarDatosReunion(mainDto, (long) votanteTrabajador.getIdVotante(), (long) JefeEquipo.getJefe().getIdVotante());
        
        return mainDto;
    }

    @Override
    public Optional<Integer> sumaTotalPesoAllIndicadorByTrabajador(int idVotante) {
        log.info("[{}-{}]", DateUtil.getYearCurrent(), idVotante);
        return indicadorRepository.sumaTotalPesoAllIndicadorByTrabajador(DateUtil.getYearCurrent(), idVotante);
    }

    @Override
    public void modificarIndicador(int id, Indicador request) {
        Indicador indicador = indicadorRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El indicador no se encuentra"));

        indicador.setDescripcion(request.getDescripcion());
        indicador.setTipoValorMeta(request.getTipoValorMeta());
        indicador.setValorMeta(request.getValorMeta());
        indicador.setPeso(request.getPeso());
        indicador.setUsuarioModificacion(authService.getIdUserSession());
        indicadorRepository.save(indicador);
    }

    @Override
    public void eliminarIndicador(int id) {
        Indicador indicador = indicadorRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El indicador no se encuentra"));
        
        // Obtener evidencias activas
        List<Evidencia> evidencias = evidenciaRepository.listEvidenciaByIdIndicador(indicador.getIdIndicador());
        
        // Verificar si alguna evidencia tiene archivo adjunto
        boolean tieneArchivosAdjuntos = evidencias.stream()
                .anyMatch(e -> org.apache.commons.lang3.StringUtils.isNotBlank(e.getSustentoRutaFile()));
        
        if (tieneArchivosAdjuntos) {
            throw new ValidationException("El indicador tiene evidencias con archivos adjuntos. Debe eliminar los archivos primero.");
        }
        
        // Marcar todas las evidencias como inactivas (estado = false)
        for (Evidencia evidencia : evidencias) {
            evidencia.setEstado(false);
            evidenciaRepository.save(evidencia);
        }
        
        // Marcar el indicador como inactivo
        indicador.setEstado(false);
        indicadorRepository.save(indicador);
    }

}
