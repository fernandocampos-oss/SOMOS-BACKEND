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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrioridadServiceImpl implements PrioridadService {

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
            if (votanteJefe.getIdSegmento() == 1) {
                modelExcelDto.setEvaluadorSegmento("DIRECTIVO");
            }
            EvaluadorResponseDto evaluado = prioridadRepository.findUsuarioById(e.getIntegrante().getIdUsuario());
            modelExcelDto.setEvaluadoNombreCompleto(e.getIntegrante().getApellidos() + " " + e.getIntegrante().getNombres());
            modelExcelDto.setEvaluadoPuesto(evaluado.getPuesto());
            UnidadOrganizativa unidadEvaluado = prioridadRepository.getUnidadByCod(evaluado.getUnidad());
            modelExcelDto.setEvaluadoCodUnidad(unidadEvaluado.getDescripcion());
            if (e.getIntegrante().getIdSegmento() == 1) {
                modelExcelDto.setEvaluadoSegmento("DIRECTIVO");
            }
            if (e.getIntegrante().getIdSegmento() == 3) {
                modelExcelDto.setEvaluadoSegmento("EJECUTOR");
            }

            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), e.getIntegrante().getIdVotante());
            List<ExcelPrioridadDto> listExcelPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                ExcelPrioridadDto modelExcelPrioridadDto = new ExcelPrioridadDto();
                modelExcelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
                modelExcelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                modelExcelPrioridadDto.setPrioridadNombre(p.getDescripcion());

                List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(e.getIntegrante().getIdVotante(), p.getIdPrioridad());

                List<ExcelIndicadorDto> listExcelIndicadorDto = new ArrayList<>();
                for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                    ExcelIndicadorDto modelExcelIndicadorDto = new ExcelIndicadorDto();
                    modelExcelIndicadorDto.setIdIndicador(i.getIdIndicador());
                    modelExcelIndicadorDto.setNombreIndicador(i.getDescripcion());
                    modelExcelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                    modelExcelIndicadorDto.setValorMeta(i.getValorMeta());
                    modelExcelIndicadorDto.setPeso(i.getPeso());

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

                        listExcelEvidenciaDto.add(modelExcelEvidenciaDto);
                    }
                    modelExcelIndicadorDto.setListEvidencia(listExcelEvidenciaDto);
                    listExcelIndicadorDto.add(modelExcelIndicadorDto);
                }
                modelExcelPrioridadDto.setListIndicador(listExcelIndicadorDto);
                listExcelPrioridadDto.add(modelExcelPrioridadDto);
            }
            modelExcelDto.setListPrioridad(listExcelPrioridadDto);
            listExcelDto.add(modelExcelDto);
        }

        return listExcelDto;
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
        List<Indicador> indicadores = indicadorRepository.getListIndicadoresByPrioridad(prioridad.getIdPrioridad());
        if (!indicadores.isEmpty()) {
            throw new ValidationException("La prioridad tiene indicador registrado");
        }
        prioridadRepository.delete(prioridad);
    }
    
    // ========================================
    // TODO: ELIMINAR ANTES DE PRODUCCIÓN - INICIO
    // Métodos de testing con IDs hardcodeados y sin validaciones de seguridad
    // ========================================
    /*
    @Override
    @Transactional
    public String inicializarDatosPrueba() {
        log.info("=== INICIANDO CREACIÓN DE DATOS DE PRUEBA ===");
        
        try {
            // IDs de usuario CORRECTOS
            Long idJefe = 249L;     // STHYWEN JAVIER
            Long idTrab1 = 388L;    // ANA PATRICIA (DNI 25572438)
            Long idTrab2 = 385L;    // LOURDES (DNI 01335296)
            
            // Buscar usuarios en BD por ID
            Usuario usuarioJefe = usuarioRepository.findById(idJefe).orElse(null);
            Usuario usuarioTrab1 = usuarioRepository.findById(idTrab1).orElse(null);
            Usuario usuarioTrab2 = usuarioRepository.findById(idTrab2).orElse(null);
            
            if (usuarioJefe == null) {
                return "Error: Usuario con ID " + idJefe + " no existe";
            }
            if (usuarioTrab1 == null) {
                return "Error: Usuario con ID " + idTrab1 + " no existe";
            }
            if (usuarioTrab2 == null) {
                return "Error: Usuario con ID " + idTrab2 + " no existe";
            }
            
            log.info("Usuarios encontrados: Jefe={} (ID:{}), Trab1={} (ID:{}), Trab2={} (ID:{})", 
                usuarioJefe.getNombres(), usuarioJefe.getIdUsuario(),
                usuarioTrab1.getNombres(), usuarioTrab1.getIdUsuario(),
                usuarioTrab2.getNombres(), usuarioTrab2.getIdUsuario());
            
            // 1. CREAR VOTANTES (si no existen)
            Votante votanteJefe = crearVotanteSiNoExiste(usuarioJefe, 5, "03"); // Segmento 5=EVALUADOR, Condicion 03
            Votante votanteTrab1 = crearVotanteSiNoExiste(usuarioTrab1, 3, "01"); // Segmento 3=EJECUTOR
            Votante votanteTrab2 = crearVotanteSiNoExiste(usuarioTrab2, 3, "01");
            
            // 2. CREAR EQUIPOS (jefe -> trabajadores)
            crearEquipoSiNoExiste(votanteJefe, votanteTrab1, (int) usuarioJefe.getIdUsuario());
            crearEquipoSiNoExiste(votanteJefe, votanteTrab2, (int) usuarioJefe.getIdUsuario());
            
            // 3. CREAR PRIORIDADES Y INDICADORES PARA LOS TRABAJADORES
            Actividad actividad = actividadRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ValidationException("No hay actividades en la BD"));
            
            TipoValorMeta tipoValor = tipoValorMetaRepository.findAllByEstado(true).stream().findFirst()
                .orElseThrow(() -> new ValidationException("No hay tipos de valor meta activos en la BD"));
            
            crearPrioridadConIndicadorSiNoExiste(votanteTrab1, actividad, tipoValor, 
                "Gestión de Calidad", "Mejora de procesos", usuarioJefe);
            crearPrioridadConIndicadorSiNoExiste(votanteTrab2, actividad, tipoValor, 
                "Atención al Paciente", "Satisfacción del usuario", usuarioJefe);
            
            log.info("=== DATOS DE PRUEBA CREADOS EXITOSAMENTE ===");
            
            return "Datos inicializados correctamente. " +
                   "Votantes: 3, Equipos: 2, Prioridades: 2. " +
                   "Usuario ID:" + votanteJefe.getIdVotante() + " puede acceder a gestion-rendimiento";
            
        } catch (Exception e) {
            log.error("Error al inicializar datos: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
    
    @Override
    @Transactional
    public String limpiarVotantesDuplicados() {
        log.info("=== LIMPIANDO VOTANTES DUPLICADOS ===");
        
        try {
            // IDs de usuario únicos que deben existir
            Long idJefe = 249L;
            Long idTrab1 = 388L;
            Long idTrab2 = 385L;
            
            // DNIs correctos
            String dniJefe = "45611148";
            String dniTrab1 = "25572438";
            String dniTrab2 = "01335296";
            
            // Para cada usuario, eliminar votantes que no tengan el DNI correcto
            List<Votante> votantesUser249 = votanteRepository.findByIdUsuario(idJefe.intValue());
            log.info("Votantes con idUsuario=249: {}", votantesUser249.size());
            
            for (Votante v : votantesUser249) {
                if (!dniJefe.equals(v.getNumeroDocumento())) {
                    log.info("Eliminando votante duplicado: ID={}, DNI={}, idUsuario={}", 
                        v.getIdVotante(), v.getNumeroDocumento(), v.getIdUsuario());
                    votanteRepository.delete(v);
                }
            }
            
            List<Votante> votantesUser388 = votanteRepository.findByIdUsuario(idTrab1.intValue());
            log.info("Votantes con idUsuario=388: {}", votantesUser388.size());
            
            for (Votante v : votantesUser388) {
                if (!dniTrab1.equals(v.getNumeroDocumento())) {
                    log.info("Eliminando votante duplicado: ID={}, DNI={}, idUsuario={}", 
                        v.getIdVotante(), v.getNumeroDocumento(), v.getIdUsuario());
                    votanteRepository.delete(v);
                }
            }
            
            List<Votante> votantesUser385 = votanteRepository.findByIdUsuario(idTrab2.intValue());
            log.info("Votantes con idUsuario=385: {}", votantesUser385.size());
            
            for (Votante v : votantesUser385) {
                if (!dniTrab2.equals(v.getNumeroDocumento())) {
                    log.info("Eliminando votante duplicado: ID={}, DNI={}, idUsuario={}", 
                        v.getIdVotante(), v.getNumeroDocumento(), v.getIdUsuario());
                    votanteRepository.delete(v);
                }
            }
            
            log.info("=== LIMPIEZA COMPLETADA ===");
            return "Limpieza completada exitosamente";
            
        } catch (Exception e) {
            log.error("Error al limpiar duplicados: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
    
    private Votante crearVotanteSiNoExiste(Usuario usuario, Integer idSegmento, String codCondicion) {
        Optional<Votante> existente = votanteRepository.findByNumeroDocumento(usuario.getNumeroDocumento());
        if (existente.isPresent()) {
            Votante votante = existente.get();
            log.info("Votante ya existe para: {} con idUsuario={} (actual). Actualizando a idUsuario={}", 
                usuario.getNumeroDocumento(), votante.getIdUsuario(), usuario.getIdUsuario());
            
            // Actualizar campos importantes
            votante.setIdUsuario((int) usuario.getIdUsuario());
            votante.setIdSegmento(idSegmento);
            votante.setCodCondicion(codCondicion);
            votante.setNombres(usuario.getNombres());
            votante.setApellidos(usuario.getApellidos() != null ? usuario.getApellidos() : "");
            
            Votante updated = votanteRepository.save(votante);
            log.info("Votante ACTUALIZADO: ID={}, idUsuario={}, Segmento={}", 
                updated.getIdVotante(), updated.getIdUsuario(), updated.getIdSegmento());
            return updated;
        }
        
        // Obtener el máximo ID actual y sumar 1
        Integer maxId = votanteRepository.findAll().stream()
            .map(Votante::getIdVotante)
            .max(Integer::compareTo)
            .orElse(0);
        
        Votante votante = new Votante();
        votante.setIdVotante(maxId + 1);
        votante.setNumeroDocumento(usuario.getNumeroDocumento());
        votante.setNombres(usuario.getNombres());
        votante.setApellidos(usuario.getApellidos() != null ? usuario.getApellidos() : "");
        votante.setIdSegmento(idSegmento);
        votante.setIdUsuario((int) usuario.getIdUsuario());
        votante.setCodCondicion(codCondicion);
        
        log.info("ANTES DE GUARDAR - Votante: idVotante={}, idUsuario={}, DNI={}, Nombre={}", 
            votante.getIdVotante(), votante.getIdUsuario(), votante.getNumeroDocumento(), votante.getNombres());
        
        Votante saved = votanteRepository.save(votante);
        
        log.info("DESPUÉS DE GUARDAR - Votante: ID={}, idUsuario={}, Nombre={}, Segmento={}", 
            saved.getIdVotante(), saved.getIdUsuario(), saved.getNombres(), saved.getIdSegmento());
        
        return saved;
    }
    
    private void crearEquipoSiNoExiste(Votante jefe, Votante integrante, Integer usuarioCreacion) {
        List<Equipo> existentes = equipoRepository.getListTrabajadoresByIdUsuarioJefeOrEvaluador(jefe.getIdVotante());
        boolean yaExiste = existentes.stream()
            .anyMatch(e -> e.getIntegrante().getIdVotante().equals(integrante.getIdVotante()));
        
        if (yaExiste) {
            log.info("Equipo ya existe: Jefe={}, Integrante={}", jefe.getNombres(), integrante.getNombres());
            return;
        }
        
        Equipo equipo = new Equipo();
        equipo.setJefe(jefe);
        equipo.setIntegrante(integrante);
        equipo.setEvaluador(jefe.getIdVotante());
        equipo.setEsActivo(true);
        equipo.setUsuarioCreacion(usuarioCreacion);
        // fechaCreacion se setea automáticamente con @PrePersist
        
        equipoRepository.save(equipo);
        log.info("Equipo creado: Jefe={}, Integrante={}", jefe.getNombres(), integrante.getNombres());
    }
    
    private void crearPrioridadConIndicadorSiNoExiste(Votante votante, Actividad actividad, 
            TipoValorMeta tipoValor, String descripcionPrioridad, String descripcionIndicador, 
            Usuario usuarioJefe) {
        // Verificar si ya tiene indicadores para 2026
        Indicador existente = indicadorRepository.getIndicadorByVotante(votante.getIdVotante());
        if (existente != null && existente.getAnio() == 2026) {
            log.info("Indicadores ya existen para: {}", votante.getNombres());
            return;
        }
        
        // Crear prioridad
        Prioridad prioridad = new Prioridad();
        prioridad.setAnio(2026);
        prioridad.setActividad(actividad);
        prioridad.setDescripcion(descripcionPrioridad);
        // fechaAsignacion se setea con @PrePersist
        
        Prioridad savedPrioridad = prioridadRepository.save(prioridad);
        log.info("Prioridad creada: ID={}, Descripcion: {}", savedPrioridad.getIdPrioridad(), descripcionPrioridad);
        
        // Crear indicador asociado
        Indicador indicador = new Indicador();
        indicador.setDescripcion(descripcionIndicador);
        indicador.setPeso(50); // 50%
        indicador.setVotante(votante);
        indicador.setTipoValorMeta(tipoValor);
        indicador.setValorMeta(100);
        indicador.setPrioridad(savedPrioridad);
        indicador.setCodRed(usuarioJefe.getCodigoRed());
        indicador.setCodUnidad(usuarioJefe.getCodigoUnidad());
        indicador.setAnio(2026);
        indicador.setEstado(true);
        indicador.setUsuarioCreacion((int) usuarioJefe.getIdUsuario());
        // fechaCreacion se setea con @PrePersist
        
        indicadorRepository.save(indicador);
        log.info("Indicador creado para: {}, Descripcion: {}", votante.getNombres(), descripcionIndicador);
    }
    */
    // ========================================
    // TODO: ELIMINAR ANTES DE PRODUCCIÓN - FIN
    // ========================================


}
