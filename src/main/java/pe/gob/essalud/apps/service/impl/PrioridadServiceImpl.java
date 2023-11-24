package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.EmailServiceClient;
import pe.gob.essalud.apps.dto.emailservice.RecuperarClaveWebRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.EmailNotificacionRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.*;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PrioridadService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private final EmailServiceClient _emailServiceClient;

    @Override
    public List<MainDto> listGestionarIndicadoresPrincipalJefe() {
        LocalDate fechaActual = LocalDate.now();
        int anioActual = fechaActual.getYear();

        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());
        log.info("cantidad de trabajadores [{}]", trabajadoresPorJefe.size());

        List<MainDto> listMainDto = new ArrayList<>();
        for (Equipo e : trabajadoresPorJefe) {
            log.info("[{}-{}]", e.getIntegrante().getIdUsuario(), e.getIntegrante().getNombres());
            MainDto modelMainDto = new MainDto();
            modelMainDto.setIdVotante(e.getIntegrante().getIdVotante());
            modelMainDto.setTrabajadorNombre(e.getIntegrante().getNombres());
            modelMainDto.setTrabajadorApellido(e.getIntegrante().getApellidos());
            EvaluadorResponseDto usuario = prioridadRepository.findUsuarioById(e.getIntegrante().getIdUsuario());
            modelMainDto.setEmail(usuario.getEmail());

            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(anioActual, e.getIntegrante().getIdVotante());
            log.info("cantidad de prioridades [{}]", prioridades.size());

            List<MainPrioridadDto> listPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                log.info("prioridad [{}]", p.getActividad().getDescripcion());
                MainPrioridadDto modelPrioridadDto = new MainPrioridadDto();
                modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());

                log.info("param indicadores [{}-{}]", e.getIntegrante().getIdUsuario(), p.getIdPrioridad());
                List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(e.getIntegrante().getIdVotante(), p.getIdPrioridad());
                log.info("indicadores por trabajador [{}]", indicadoresPorTrabajadorYPrioridad.size());

                List<MainIndicadorDto> listIndicadorDto = new ArrayList<>();
                for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                    log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                    MainIndicadorDto modelIndicadorDto = new MainIndicadorDto();
                    modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                    modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                    modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                    modelIndicadorDto.setValorMeta(i.getValorMeta());
                    modelIndicadorDto.setPeso(i.getPeso());

                    List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());
                    log.info("cantidad de tareas por indicador [{}]", listEvidencia.size());

                    List<MainEvidenciaDto> listEvidenciaDto = new ArrayList<>();
                    for (Evidencia t : listEvidencia) {
                        log.info("[{}]", t.getDescripcion());
                        MainEvidenciaDto modelEvidenciaDto = new MainEvidenciaDto();
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
            modelMainDto.setListPrioridad(listPrioridadDto);
            listMainDto.add(modelMainDto);
        }
        return listMainDto;
    }

    @Override
    public List<Actividad> getAllActividades() {
        return actividadRepository.findAll();
    }

    @Override
    public List<ExcelDto> generarExcelDirectivo() {
        LocalDate fechaActual = LocalDate.now();
        int anioActual = fechaActual.getYear();

        EvaluadorResponseDto evaluador = prioridadRepository.findUsuarioById(authService.getIdUserSession());
        Votante votanteJefe = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());

        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());
        log.info("cantidad de trabajadores [{}]", trabajadoresPorJefe.size());

        List<ExcelDto> listExcelDto = new ArrayList<>();
        for (Equipo e : trabajadoresPorJefe) {
            log.info("usuario-nombre  [{}-{}]", e.getIntegrante().getIdUsuario(), e.getIntegrante().getNombres());
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
//            if (e.getIntegrante().getIdSegmento() == 1) {
//                modelExcelDto.setEvaluadoSegmento("EJECUTOR");
//            }
            if (e.getIntegrante().getIdSegmento() == 3) {
                modelExcelDto.setEvaluadoSegmento("EJECUTOR");
            } else {
                modelExcelDto.setEvaluadoSegmento("");
            }

            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(anioActual, e.getIntegrante().getIdVotante());
            List<ExcelPrioridadDto> listExcelPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                log.info("actividad prioridad [{}]", p.getActividad().getDescripcion());
                ExcelPrioridadDto modelExcelPrioridadDto = new ExcelPrioridadDto();
                modelExcelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                modelExcelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());

                List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(e.getIntegrante().getIdVotante(), p.getIdPrioridad());
                log.info("indicadores por trabajador [{}]", indicadoresPorTrabajadorYPrioridad.size());

                List<ExcelIndicadorDto> listExcelIndicadorDto = new ArrayList<>();
                for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                    log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                    ExcelIndicadorDto modelExcelIndicadorDto = new ExcelIndicadorDto();
                    modelExcelIndicadorDto.setIdIndicador(i.getIdIndicador());
                    modelExcelIndicadorDto.setNombreIndicador(i.getDescripcion());
                    modelExcelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                    modelExcelIndicadorDto.setValorMeta(i.getValorMeta());
                    modelExcelIndicadorDto.setPeso(i.getPeso());

                    List<Evidencia> listEvidencia = evidenciaRepository.listEvidenciaByIdIndicador(i.getIdIndicador());
                    log.info("cantidad de tareas por indicador [{}]", listEvidencia.size());

                    List<ExcelEvidenciaDto> listExcelTareaDto = new ArrayList<>();
                    for (Evidencia t : listEvidencia) {
                        log.info("[{}]", t.getDescripcion());
                        ExcelEvidenciaDto modelExcelTareaDto = new ExcelEvidenciaDto();
                        modelExcelTareaDto.setIdEvidencia(t.getIdEvidencia());
                        modelExcelTareaDto.setNombre(t.getDescripcion());
                        modelExcelTareaDto.setPlazo(t.getPlazo());

                        modelExcelTareaDto.setFechaCreacion(t.getFechaCreacion());
                        modelExcelTareaDto.setEvidenciaDescripcion(t.getSustentoDescripcion());
                        modelExcelTareaDto.setEvidenciaFechaRegistro(t.getSustentoFechaRegistro());
                        modelExcelTareaDto.setEvidenciaExtensionFile(t.getSustentoExtensionFile());

                        listExcelTareaDto.add(modelExcelTareaDto);
                    }
                    modelExcelIndicadorDto.setListTarea(listExcelTareaDto);
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

}
