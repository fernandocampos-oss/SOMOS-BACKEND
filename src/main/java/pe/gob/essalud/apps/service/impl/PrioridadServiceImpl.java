package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.RedPersonalRepository;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.VotanteRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PrioridadService;

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

}
