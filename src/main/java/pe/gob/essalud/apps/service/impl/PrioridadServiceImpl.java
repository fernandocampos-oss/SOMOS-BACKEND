package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.client.EmailServiceClient;
import pe.gob.essalud.apps.common.util.DateUtil;
import pe.gob.essalud.apps.dto.emailservice.RecuperarClaveWebRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EmailNotificacionRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdatePrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.reporteGdrRequest.ReporteSeguimientoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.*;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.reporteGdrResponse.ReporteMatrizResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.reporteGdrResponse.ReporteSeguimientoResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.RedPersonalRepository;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.VotanteRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PrioridadService;
import pe.gob.essalud.apps.repository.gdr.SegmentoGdrRepository;
import pe.gob.essalud.apps.model.gdr.SegmentoGdr;
import pe.gob.essalud.apps.repository.gdr.ReunionEstablecimientoMetasRepository;
import pe.gob.essalud.apps.model.gdr.ReunionEstablecimientoMetas;
import pe.gob.essalud.apps.service.gdr.SentidoIndicadorService;
import pe.gob.essalud.apps.service.gdr.ValorAlcanzadoPrioridadService;
import pe.gob.essalud.apps.repository.gdr.ComentarioEstadoRepository;
import pe.gob.essalud.apps.model.gdr.ComentarioEstado;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrioridadServiceImpl implements PrioridadService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PrioridadRepository prioridadRepository;
    private final ActividadRepository actividadRepository;
    private final AuthService authService;
    private final EquipoRepository equipoRepository;
    private final IndicadorRepository indicadorRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;
    private final RedPersonalRepository redPersonalRepository;
    private final VotanteRepository votanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoValorMetaRepository tipoValorMetaRepository;
    private final EmailServiceClient _emailServiceClient;
    private final SegmentoGdrRepository segmentoGdrRepository;
    private final ReunionEstablecimientoMetasRepository reunionMetasRepository;
    private final SentidoIndicadorService sentidoIndicadorService;
    private final ValorAlcanzadoPrioridadService valorAlcanzadoPrioridadService;
    private final ComentarioEstadoRepository comentarioEstadoRepository;

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
     * Obtener datos de reunión establecimiento de metas para el Excel (ExcelDto)
     */
    private void llenarDatosReunionExcelDto(ExcelDto dto, Long idVotanteEvaluado, Long idVotanteEvaluador) {
        try {
            String periodo = String.valueOf(DateUtil.getYearCurrent());
            log.info("llenarDatosReunionExcelDto: evaluado={}, evaluador={}, periodo={}", idVotanteEvaluado, idVotanteEvaluador, periodo);
            
            Optional<ReunionEstablecimientoMetas> reunionOpt = reunionMetasRepository
                .findByIdVotanteEvaluadoAndIdVotanteEvaluadorAndPeriodo(idVotanteEvaluado, idVotanteEvaluador, periodo);
            
            log.info("llenarDatosReunionExcelDto: reunionOpt.isPresent={}", reunionOpt.isPresent());
            
            if (reunionOpt.isPresent()) {
                ReunionEstablecimientoMetas reunion = reunionOpt.get();
                log.info("llenarDatosReunionExcelDto: asistio={}, fechaReunion={}", reunion.getAsistio(), reunion.getFechaReunion());
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
            log.info("llenarDatosReunionExcelDto: reunionAsistio={}, reunionFecha={}", dto.getReunionAsistio(), dto.getReunionFecha());
        } catch (Exception e) {
            log.error("Error al llenar datos de reunión: {}", e.getMessage(), e);
            dto.setReunionAsistio("No");
            dto.setReunionFecha("-");
        }
    }

    @Override
    public List<MainDto> listGestionarIndicadoresPrincipalJefe() {
        Votante votante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
//        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());
        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefeOrEvaluador(votante.getIdVotante());

        List<MainDto> listMainDto = new ArrayList<>();
        for (Equipo e : trabajadoresPorJefe) {
            log.info("[{}-{}]", e.getIntegrante().getIdUsuario(), e.getIntegrante().getNombres());
            MainDto modelMainDto = new MainDto();
            modelMainDto.setIdVotante(e.getIntegrante().getIdVotante());
            modelMainDto.setTrabajadorNombre(e.getIntegrante().getNombres());
            modelMainDto.setTrabajadorApellido(e.getIntegrante().getApellidos());
            // Obtener segmento del trabajador desde tabla segmento_gdr
            modelMainDto.setEvaluadoSegmento(obtenerSegmento(e.getIntegrante().getNumeroDocumento(), e.getIntegrante().getIdSegmento()));
            EvaluadorResponseDto usuario = prioridadRepository.findUsuarioById(e.getIntegrante().getIdUsuario());
            modelMainDto.setEmail(usuario.getEmail());
            int porcentajeTotal = 0;

            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), e.getIntegrante().getIdVotante());

            List<MainPrioridadDto> listPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                MainPrioridadDto modelPrioridadDto = new MainPrioridadDto();
                modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                if(p.getDescripcion() == null) {
                	modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());
                }else{
                	modelPrioridadDto.setPrioridadNombre(p.getDescripcion());
                }
                
                modelPrioridadDto.setIdActividad(p.getActividad().getIdActividad());
                modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());

                List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(e.getIntegrante().getIdVotante(), p.getIdPrioridad());

                List<MainIndicadorDto> listIndicadorDto = new ArrayList<>();
                for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                    log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                    MainIndicadorDto modelIndicadorDto = new MainIndicadorDto();
                    modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                    modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                    modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                    modelIndicadorDto.setIdTipoValorMeta(i.getTipoValorMeta().getIdTipoValorMeta());
                    modelIndicadorDto.setValorMeta(i.getValorMeta());
                    modelIndicadorDto.setPeso(i.getPeso());
                    /* Agregado de 2 columnas - Inicio */
                    modelIndicadorDto.setDesPrioridad(i.getDesPrioridad());
                    modelIndicadorDto.setFlDesPrioridad(i.getFlDesPrioridad());
                    /* Agregado de 2 columnas - Fin */
                    int numero = 0;
                    numero = i.getPeso();
                    porcentajeTotal += numero;

                    List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                    List<MainEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                    for (Evidencia t : listEvidencia) {
                        log.info("[{}]", t.getDescripcion());
                        MainEvidenciaDto modelEvidenciaDto = new MainEvidenciaDto();
                        modelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                        modelEvidenciaDto.setDescripcion(t.getDescripcion());
                        modelEvidenciaDto.setPlazo(t.getPlazo());

                        modelEvidenciaDto.setComentario(t.getComentario());
                        modelEvidenciaDto.setCalificacion(t.getCalificacion());

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
            modelMainDto.setListPrioridad(listPrioridadDto);
            listMainDto.add(modelMainDto);

            modelMainDto.setPesoTotal(porcentajeTotal);
        }
        return listMainDto;
    }

    @Override
    public List<Actividad> getAllActividades() {
        return actividadRepository.findAll();
    }

    @Override
    public List<ExcelDto> generarExcelDirectivo() {

        EvaluadorResponseDto evaluador = prioridadRepository.findUsuarioById(authService.getIdUserSession());
        Votante votanteJefe = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());

        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());

        List<ExcelDto> listExcelDto = new ArrayList<>();
        for (Equipo e : trabajadoresPorJefe) {
            log.info("[{}-{}]", e.getIntegrante().getIdUsuario(), e.getIntegrante().getNombres());
            ExcelDto modelExcelDto = new ExcelDto();
            modelExcelDto.setEvaluadorNombreCompleto(evaluador.getApellidos() + " " + evaluador.getNombres());
            modelExcelDto.setEvaluadorPuesto(evaluador.getPuesto());
            UnidadOrganizativa unidadEvaluador = prioridadRepository.getUnidadByCod(evaluador.getUnidad());
            modelExcelDto.setEvaluadorCodUnidad(unidadEvaluador.getDescripcion());
            modelExcelDto.setEvaluadorNumeroDocumento(evaluador.getNumeroDocumento());
            modelExcelDto.setEvaluadorSegmento(obtenerSegmento(evaluador.getNumeroDocumento(), votanteJefe.getIdSegmento()));
            EvaluadorResponseDto evaluado = prioridadRepository.findUsuarioById(e.getIntegrante().getIdUsuario());
            modelExcelDto.setEvaluadoNombreCompleto(e.getIntegrante().getApellidos() + " " + e.getIntegrante().getNombres());
            modelExcelDto.setEvaluadoPuesto(evaluado.getPuesto());
            UnidadOrganizativa unidadEvaluado = prioridadRepository.getUnidadByCod(evaluado.getUnidad());
            modelExcelDto.setEvaluadoCodUnidad(unidadEvaluado.getDescripcion());
            modelExcelDto.setEvaluadoNumeroDocumento(e.getIntegrante().getNumeroDocumento());
            modelExcelDto.setEvaluadoSegmento(obtenerSegmento(e.getIntegrante().getNumeroDocumento(), e.getIntegrante().getIdSegmento()));

            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), e.getIntegrante().getIdVotante());
            
            // Obtener IDs para batch queries
            List<Long> idsPrioridades = prioridades.stream()
                .map(p -> (long) p.getIdPrioridad())
                .collect(Collectors.toList());
            
            // Obtener valores alcanzados en batch
            Map<Long, BigDecimal> valoresAlcanzados = !idsPrioridades.isEmpty() 
                ? valorAlcanzadoPrioridadService.obtenerMultiples(idsPrioridades)
                : Map.of();
            
            // Recolectar IDs de indicadores para obtener sentidos
            List<Long> idsIndicadores = new ArrayList<>();
            for (Prioridad p : prioridades) {
                List<Indicador> indicadores = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(
                    e.getIntegrante().getIdVotante(), p.getIdPrioridad());
                for (Indicador i : indicadores) {
                    idsIndicadores.add((long) i.getIdIndicador());
                }
            }
            
            // Obtener sentidos en batch
            Map<Long, String> sentidosMap = !idsIndicadores.isEmpty() 
                ? sentidoIndicadorService.obtenerSentidosPorIndicadores(idsIndicadores)
                : Map.of();
            
            // Recolectar IDs de evidencias para obtener comentarios
            List<Long> idsEvidencias = new ArrayList<>();
            for (Long idIndicador : idsIndicadores) {
                List<Evidencia> evidencias = evidenciaRepository.listEvidenciaByIdIndicador(idIndicador.intValue());
                for (Evidencia ev : evidencias) {
                    idsEvidencias.add((long) ev.getIdEvidencia());
                }
            }
            
            // Obtener comentarios de estado en batch (todos los tipos)
            Map<Long, ComentarioEstado> comentariosIndividualesMap = new java.util.HashMap<>();
            Map<Long, ComentarioEstado> comentariosFinalesMap = new java.util.HashMap<>();
            if (!idsEvidencias.isEmpty()) {
                List<ComentarioEstado> todosComentarios = comentarioEstadoRepository.findByIdEvidenciaIn(idsEvidencias);
                for (ComentarioEstado c : todosComentarios) {
                    if ("final".equals(c.getTipoComentario())) {
                        comentariosFinalesMap.put(c.getIdEvidencia(), c);
                    } else {
                        comentariosIndividualesMap.put(c.getIdEvidencia(), c);
                    }
                }
            }
            
            List<ExcelPrioridadDto> listExcelPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                ExcelPrioridadDto modelExcelPrioridadDto = new ExcelPrioridadDto();
                modelExcelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
                modelExcelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                modelExcelPrioridadDto.setPrioridadNombre(p.getDescripcion());
                
                // Obtener valor alcanzado para esta prioridad
                BigDecimal valorAlcanzadoPrioridad = valoresAlcanzados.getOrDefault((long) p.getIdPrioridad(), BigDecimal.ZERO);

                List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(e.getIntegrante().getIdVotante(), p.getIdPrioridad());

                List<ExcelIndicadorDto> listExcelIndicadorDto = new ArrayList<>();
                for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                    ExcelIndicadorDto modelExcelIndicadorDto = new ExcelIndicadorDto();
                    modelExcelIndicadorDto.setIdIndicador(i.getIdIndicador());
                    modelExcelIndicadorDto.setNombreIndicador(i.getDescripcion());
                    modelExcelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                    modelExcelIndicadorDto.setValorMeta(i.getValorMeta());
                    modelExcelIndicadorDto.setPeso(i.getPeso());
                    
                    // Sentido del indicador
                    String sentido = sentidosMap.getOrDefault((long) i.getIdIndicador(), "ascendente");
                    modelExcelIndicadorDto.setSentido("ascendente".equalsIgnoreCase(sentido) ? "Ascendente" : "Descendente");
                    
                    // Valor alcanzado (por prioridad, compartido entre indicadores de la misma prioridad)
                    modelExcelIndicadorDto.setValorAlcanzado(valorAlcanzadoPrioridad);
                    
                    // Calcular puntaje por meta
                    BigDecimal puntaje = calcularPuntajePorMeta(sentido, valorAlcanzadoPrioridad, 
                        BigDecimal.valueOf(i.getValorMeta()), BigDecimal.valueOf(i.getPeso()));
                    modelExcelIndicadorDto.setPuntajePorMeta(puntaje);

                    List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());

                    List<ExcelEvidenciaDto> listExcelEvidenciaDto = new ArrayList<>();
                    for (Evidencia t : listEvidencia) {
                        ExcelEvidenciaDto modelExcelEvidenciaDto = new ExcelEvidenciaDto();
                        modelExcelEvidenciaDto.setIdEvidencia(t.getIdEvidencia());
                        modelExcelEvidenciaDto.setDescripcion(t.getDescripcion());
                        modelExcelEvidenciaDto.setPlazo(t.getPlazo());
                        modelExcelEvidenciaDto.setComentario(t.getComentario());
                        modelExcelEvidenciaDto.setFechaCreacion(t.getFechaCreacion());
                        modelExcelEvidenciaDto.setSustentoDescripcion(t.getSustentoDescripcion());
                        modelExcelEvidenciaDto.setSustentoFechaRegistro(t.getSustentoFechaRegistro());
                        modelExcelEvidenciaDto.setSustentoExtensionFile(t.getSustentoExtensionFile());
                        
                        // Identificar si es SUSTENTO FINAL (comparación exacta como en frontend)
                        boolean esFinal = "SUSTENTO FINAL".equals(t.getDescripcion());
                        modelExcelEvidenciaDto.setEsEvidenciaFinal(esFinal);
                        
                        // Obtener estado y comentario adicional de la tabla comentario_estado
                        // Usar el mapa correcto según si es evidencia final o individual
                        ComentarioEstado comentarioEstado = esFinal 
                            ? comentariosFinalesMap.get((long) t.getIdEvidencia())
                            : comentariosIndividualesMap.get((long) t.getIdEvidencia());
                        if (comentarioEstado != null) {
                            modelExcelEvidenciaDto.setEstadoDropdown(comentarioEstado.getEstadoDropdown());
                            modelExcelEvidenciaDto.setComentarioAdicional(comentarioEstado.getComentarioAdicional());
                        }

                        listExcelEvidenciaDto.add(modelExcelEvidenciaDto);
                    }
                    
                    // Ordenar: evidencias normales primero, SUSTENTO FINAL al último
                    listExcelEvidenciaDto.sort(Comparator.comparing(ExcelEvidenciaDto::isEsEvidenciaFinal));
                    
                    modelExcelIndicadorDto.setListEvidencia(listExcelEvidenciaDto);
                    listExcelIndicadorDto.add(modelExcelIndicadorDto);
                }
                modelExcelPrioridadDto.setListIndicador(listExcelIndicadorDto);
                listExcelPrioridadDto.add(modelExcelPrioridadDto);
            }
            modelExcelDto.setListPrioridad(listExcelPrioridadDto);
            
            // Llenar datos de reunión establecimiento de metas
            llenarDatosReunionExcelDto(modelExcelDto, (long) e.getIntegrante().getIdVotante(), (long) votanteJefe.getIdVotante());
            
            listExcelDto.add(modelExcelDto);
        }

        return listExcelDto;
    }
    
    /**
     * Calcular puntaje por meta según fórmula del frontend
     * Ascendente: (valorAlcanzado / valorMeta) * 100 * (peso/100)
     * Descendente: ((1 - (valorAlcanzado / valorMeta)) + 1) * 100 * (peso/100)
     * El puntaje máximo es peso * 100 / 100 = peso
     */
    private BigDecimal calcularPuntajePorMeta(String sentido, BigDecimal valorAlcanzado, BigDecimal valorMeta, BigDecimal peso) {
        if (valorAlcanzado == null || valorMeta == null || valorMeta.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal puntaje;
        BigDecimal pesoDecimal = peso.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal maxPuntaje = peso; // Puntaje máximo = peso
        
        if ("ascendente".equalsIgnoreCase(sentido)) {
            // (valorAlcanzado / valorMeta) * 100 * (peso/100) = (valorAlcanzado / valorMeta) * peso
            puntaje = valorAlcanzado.divide(valorMeta, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .multiply(pesoDecimal);
        } else {
            // Descendente: ((1 - (valorAlcanzado / valorMeta)) + 1) * 100 * (peso/100)
            BigDecimal ratio = valorAlcanzado.divide(valorMeta, 4, RoundingMode.HALF_UP);
            // Si el ratio es mayor a 2, puntaje es 0
            if (ratio.compareTo(BigDecimal.valueOf(2)) > 0) {
                return BigDecimal.ZERO;
            }
            puntaje = BigDecimal.ONE.subtract(ratio).add(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100))
                .multiply(pesoDecimal);
        }
        
        // No puede exceder el puntaje máximo
        if (puntaje.compareTo(maxPuntaje) > 0) {
            puntaje = maxPuntaje;
        }
        
        return puntaje.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void sendCorreoNotificacion(EmailNotificacionRequestDto requestDto) {
        String correo = requestDto.getEmail();
        String url = requestDto.getUrl();
        _sendMailRecuperarClave(correo, url);
    }

    @Async
    protected void _sendMailRecuperarClave(String correo, String url) {
        RecuperarClaveWebRequestDto requestRecuperarClave = new RecuperarClaveWebRequestDto();
        requestRecuperarClave.setEmail(correo);
        requestRecuperarClave.setUrl(url);
        _emailServiceClient.recuperarClave(requestRecuperarClave);
    }

    @Override
    public List<UnidadOrganizativa> getAllUnidadesOrganizativas() {
        return unidadOrganizativaRepository.findAll();
    }

    @Override
    public List<ReporteSeguimientoResponseDto> reporteSeguimientoGdr(ReporteSeguimientoRequestDto requestDto) {
        List<Indicador> listIndicador = new ArrayList<>();
        if (requestDto.getAllRed().equals(true)) {
            List<RedPersonal> listRed = redPersonalRepository.findAll();
            ArrayList<String> listAllRed = new ArrayList<>();
            for (RedPersonal red : listRed) {
                listAllRed.add(red.getCodRed());
            }
            log.info("param reporte todas las redes [{}-{}-{}]", requestDto.getAnio(), listAllRed, requestDto.getCodUnidad());
            listIndicador = prioridadRepository.reporteSeguimientoGdr(requestDto.getAnio(), listAllRed, requestDto.getCodUnidad());
        } else {
            log.info("param reporte seguimiento [{}-{}-{}]", requestDto.getAnio(), requestDto.getListCodRed(), requestDto.getCodUnidad());
            listIndicador = prioridadRepository.reporteSeguimientoGdr(requestDto.getAnio(), requestDto.getListCodRed(), requestDto.getCodUnidad());
        }

        List<ReporteSeguimientoResponseDto> listSeguimiento = new ArrayList<>();
        for (Indicador i : listIndicador) {
            ReporteSeguimientoResponseDto reporteSeguimientoResponseDto = new ReporteSeguimientoResponseDto();

            List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());
            for (Evidencia e : listEvidencia) {
                reporteSeguimientoResponseDto.setNumeroDocumento(i.getVotante().getNumeroDocumento());
                reporteSeguimientoResponseDto.setNombreCompleto(i.getVotante().getApellidos() + " " + i.getVotante().getNombres());
                UnidadOrganizativa unidadVotante = prioridadRepository.getUnidadByCod(i.getCodUnidad());
                reporteSeguimientoResponseDto.setUnidad(unidadVotante.getDescripcion());
                EvaluadorResponseDto getUsuario = prioridadRepository.findUsuarioById(i.getVotante().getIdUsuario());

                reporteSeguimientoResponseDto.setPuesto(getUsuario.getPuesto());
                if (i.getVotante().getIdSegmento() == 1) {
                    reporteSeguimientoResponseDto.setRol("Evaluador/a");
                }
                if (i.getVotante().getIdSegmento() == 3) {
                    reporteSeguimientoResponseDto.setRol("Evaluado/a");
                }
                reporteSeguimientoResponseDto.setMeta(e.getDescripcion());
                reporteSeguimientoResponseDto.setMes(DateUtil.getMonthString(e.getPlazo()));
                reporteSeguimientoResponseDto.setPlazo(DateUtil.format(e.getPlazo()));
            }
            listSeguimiento.add(reporteSeguimientoResponseDto);
        }

        return listSeguimiento;
    }

    @Override
    public List<ReporteMatrizResponseDto> reporteMatrizGdr(ReporteSeguimientoRequestDto requestDto) {
        List<Integer> listIdsVotantes = new ArrayList<>();
        if (requestDto.getAllRed().equals(true)) {
            List<RedPersonal> listRed = redPersonalRepository.findAll();
            ArrayList<String> listAllRed = new ArrayList<>();
            for (RedPersonal red : listRed) {
                listAllRed.add(red.getCodRed());
            }
            log.info("votantes por todas las redes [{}-{}-{}]", requestDto.getAnio(), listAllRed, requestDto.getCodUnidad());
            listIdsVotantes = indicadorRepository.reporteMatrizGdrFindVontates(requestDto.getAnio(), listAllRed, requestDto.getCodUnidad());
        } else {
            log.info("votantes por red [{}-{}-{}]", requestDto.getAnio(), requestDto.getListCodRed(), requestDto.getCodUnidad());
            listIdsVotantes = indicadorRepository.reporteMatrizGdrFindVontates(requestDto.getAnio(), requestDto.getListCodRed(), requestDto.getCodUnidad());
        }

        List<ReporteMatrizResponseDto> listMatriz = new ArrayList<>();
        for (int idVotante : listIdsVotantes) {
            ReporteMatrizResponseDto reporteMatrizResponseDto = new ReporteMatrizResponseDto();
            Indicador indicador = indicadorRepository.getIndicadorByVotante(idVotante);

            Optional<Votante> votante = votanteRepository.findById(idVotante);
            reporteMatrizResponseDto.setNumeroDocumento(votante.get().getNumeroDocumento());
            reporteMatrizResponseDto.setNombreCompleto(votante.get().getApellidos() + " " + votante.get().getNombres());

            EvaluadorResponseDto usuario = prioridadRepository.findUsuarioById(votante.get().getIdUsuario());
            reporteMatrizResponseDto.setFechaNacimiento(usuario.getFechaNacimiento());
            reporteMatrizResponseDto.setRegimenLaboral(usuario.getRegimen());
            reporteMatrizResponseDto.setCorreo(usuario.getEmail());
            if (!usuario.getGenero().isEmpty()) {
                reporteMatrizResponseDto.setGenero(usuario.getGenero().substring(0, 1));
            } else {
                reporteMatrizResponseDto.setGenero("");
            }

            UnidadOrganizativa unidadVotante = prioridadRepository.getUnidadByCod(indicador.getCodUnidad());
            reporteMatrizResponseDto.setOrgano(unidadVotante.getDescripcion());
            reporteMatrizResponseDto.setUnidad("");
            reporteMatrizResponseDto.setPuesto(usuario.getPuesto());
            if (votante.get().getIdSegmento() == 1) {
                reporteMatrizResponseDto.setSegmento("Directivo");
                reporteMatrizResponseDto.setRol("Evaluador/a");
            }
            if (votante.get().getIdSegmento() == 3) {
                reporteMatrizResponseDto.setSegmento("Ejecutor");
                reporteMatrizResponseDto.setRol("Evaluado/a");
            }

            List<String> listDescripcionIndicador = indicadorRepository.listIndicadorDescripcionByVotante(idVotante);
            ArrayList<String> listIndicadorNombres = new ArrayList<String>();
            for (String descripcion : listDescripcionIndicador) {
                listIndicadorNombres.add(descripcion);
            }
            reporteMatrizResponseDto.setListDescripcionIndicador(listIndicadorNombres);
            listMatriz.add(reporteMatrizResponseDto);
        }

        return listMatriz;
    }

    @Override
    public void modificarPrioridad(int id, UpdatePrioridadDto requestDto) {
        if (requestDto != null) {
            Prioridad prioridad = prioridadRepository.findById(id)
                    .orElseThrow(() -> new ValidationException("No se encontro prioridad"));

            prioridad.setActividad(requestDto.getActividad());
            prioridad.setFechaAsignacion(requestDto.getFechaAsignacion());
            prioridad.setDescripcion(requestDto.getPrioridadNombre());
            prioridadRepository.save(prioridad);
        }
    }

    @Override
    public void eliminarPrioridad(int id) {
        Prioridad prioridad = prioridadRepository.findById(id)
                .orElseThrow(() -> new ValidationException("La prioridad no se encuentra"));
        
        // Obtener todos los indicadores de la prioridad
        List<Indicador> indicadores = indicadorRepository.getListIndicadoresByPrioridad(prioridad.getIdPrioridad());
        
        // Eliminar en cascada: indicadores y sus evidencias (soft delete)
        for (Indicador indicador : indicadores) {
            // Obtener evidencias del indicador
            List<Evidencia> evidencias = evidenciaRepository.listEvidenciaByIdIndicador(indicador.getIdIndicador());
            
            // Marcar evidencias como inactivas
            for (Evidencia evidencia : evidencias) {
                evidencia.setEstado(false);
                evidenciaRepository.save(evidencia);
            }
            
            // Marcar indicador como inactivo
            indicador.setEstado(false);
            indicadorRepository.save(indicador);
        }
        
        // Marcar prioridad como inactiva (soft delete)
        prioridad.setEstado(false);
        prioridadRepository.save(prioridad);
    }
    
}
