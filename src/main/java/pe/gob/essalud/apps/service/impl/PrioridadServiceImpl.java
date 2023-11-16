package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.*;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.PrioridadService;

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
    private final TareaRepository tareaRepository;

    @Override
    public List<MainDto> getPrioridadPorTrabajadorEnGestionJefe() {
        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());
        log.info("cantidad de trabajadores [{}]", trabajadoresPorJefe.size());

        List<MainDto> listMainDto = new ArrayList<>();
        for (Equipo e : trabajadoresPorJefe) {
            log.info("[{}-{}]", e.getIntegrante().getIdUsuario(), e.getIntegrante().getNombres());
            MainDto modelMainDto = new MainDto();
            modelMainDto.setTrabajadorNombre(e.getIntegrante().getNombres());
            modelMainDto.setTrabajadorApellido(e.getIntegrante().getApellidos());

            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(e.getIntegrante().getIdUsuario());
            log.info("cantidad de prioridades [{}]", prioridades.size());
            List<MainPrioridadDto> listPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                log.info("actividad prioridad [{}]", p.getActividad().getDescripcion());
                MainPrioridadDto modelPrioridadDto = new MainPrioridadDto();
                modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());

                log.info("param indicadores [{}-{}]", e.getIntegrante().getIdUsuario(), p.getIdPrioridad());
                List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(e.getIntegrante().getIdUsuario(), p.getIdPrioridad());
                log.info("indicadores por trabajador [{}]", indicadoresPorTrabajadorYPrioridad.size());

                List<MainIndicadorDto> listIndicadorDto = new ArrayList<>();
                for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                    log.info("[{}-{}]", i.getIdIndicador(), i.getNombre());
                    MainIndicadorDto modelIndicadorDto = new MainIndicadorDto();
                    modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                    modelIndicadorDto.setNombreIndicador(i.getNombre());
                    modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                    modelIndicadorDto.setValorMeta(i.getValorMeta());
                    modelIndicadorDto.setPeso(i.getPeso());

                    List<Tarea> listTarea = tareaRepository.getTareasByIdIndicador(i.getIdIndicador());
                    log.info("cantidad de tareas por indicador [{}]", listTarea.size());

                    List<MainTareaDto> listTareaDto = new ArrayList<>();
                    for (Tarea t : listTarea) {
                        log.info("[{}]", t.getNombre());
                        MainTareaDto modelTareaDto = new MainTareaDto();
                        modelTareaDto.setIdTarea(t.getIdTarea());
                        modelTareaDto.setNombre(t.getNombre());
                        modelTareaDto.setPlazo(t.getPlazo());

                        modelTareaDto.setFechaCreacion(t.getFechaCreacion());
                        modelTareaDto.setMotivoRechazo(t.getMotivoRechazo());
                        modelTareaDto.setEvidenciaDescripcion(t.getEvidenciaDescripcion());
                        modelTareaDto.setEvidenciaFechaRegistro(t.getEvidenciaFechaRegistro());
                        modelTareaDto.setEvidenciaExtensionFile(t.getEvidenciaExtensionFile());

                        listTareaDto.add(modelTareaDto);
                    }
                    modelIndicadorDto.setListTarea(listTareaDto);
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
    public List<Indicador> getAllIndicadorOrganizar() {
        return prioridadRepository.getAllIndicadorOrganizar();
    }

    @Override
    public void actualizarPrioridadEnListaIndicadores(PrioridadDto prioridadDto) {
        log.info("idActividad [{}]", prioridadDto.getActividad().getIdActividad());
        log.info("idListIndicadores [{}]", prioridadDto.getListIdIndicador());

        Prioridad model = new Prioridad();
        model.setActividad(prioridadDto.getActividad());

        LocalDate fechaActualTmp = LocalDate.now();
        int anioRegistro = fechaActualTmp.getYear();
        model.setAnioRegistro(anioRegistro);

        Prioridad result = prioridadRepository.save(model);
        log.info("result [{}]", result);

        prioridadRepository.actualizarPrioridadEnListaIndicadores(result.getIdPrioridad(), prioridadDto.getListIdIndicador());

    }

    @Override
    public List<Actividad> getAllActividades() {
        return actividadRepository.findAll();
    }

//    @Override
//    public int finalizarTareaAdministrador(Number idRequerimientoUsuario) {
//        return indicadorUsuarioRepository.finalizarTareaAdministrador(LocalDateTime.now(ZoneId.of("America/Lima")), idRequerimientoUsuario);
//    }

    @Override
    public List<ExcelDto> generarExcelDirectivo() {
        int idUserSession = authService.getIdUserSession();
        EvaluadorResponseDto evaluador = prioridadRepository.findUsuarioById(idUserSession);

        Votante votanteJefe = equipoRepository.getVotanteByIdUsuario(idUserSession);

        List<Equipo> trabajadoresPorJefe = equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());
        log.info("cantidad de trabajadores [{}]", trabajadoresPorJefe.size());

        List<ExcelDto> listExcelDto = new ArrayList<>();
        for (Equipo e : trabajadoresPorJefe) {
            log.info("[{}-{}]", e.getIntegrante().getIdUsuario(), e.getIntegrante().getNombres());
            ExcelDto modelExcelDto = new ExcelDto();
            modelExcelDto.setEvaluadorNombreCompleto(evaluador.getApellidos() + " " + evaluador.getNombres());
            modelExcelDto.setEvaluadorPuesto(evaluador.getPuesto());
            modelExcelDto.setEvaluadorCodUnidad(evaluador.getUnidad());
            modelExcelDto.setEvaluadorNumeroDocumento(evaluador.getNumeroDocumento());
            if (votanteJefe.getIdSegmento() == 1) {
                modelExcelDto.setEvaluadorSegmento("DIRECTIVO");
            }
            EvaluadorResponseDto evaluado = prioridadRepository.findUsuarioById(e.getIntegrante().getIdUsuario());
            modelExcelDto.setEvaluadoNombreCompleto(e.getIntegrante().getApellidos() + " " + e.getIntegrante().getNombres() );
            modelExcelDto.setEvaluadoPuesto(evaluado.getPuesto());
            modelExcelDto.setEvaluadoCodUnidad(evaluado.getUnidad());
            if (e.getIntegrante().getIdSegmento() == 1) {
                modelExcelDto.setEvaluadoSegmento("EJECUTOR");
            }
            if (e.getIntegrante().getIdSegmento() == 3) {
                modelExcelDto.setEvaluadoSegmento("EJECUTOR");
            }

            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(e.getIntegrante().getIdUsuario());
            List<ExcelPrioridadDto> listExcelPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                log.info("actividad prioridad [{}]", p.getActividad().getDescripcion());
                ExcelPrioridadDto modelExcelPrioridadDto = new ExcelPrioridadDto();
                modelExcelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                modelExcelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());

                List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(e.getIntegrante().getIdUsuario(), p.getIdPrioridad());
                log.info("indicadores por trabajador [{}]", indicadoresPorTrabajadorYPrioridad.size());

                List<ExcelIndicadorDto> listExcelIndicadorDto = new ArrayList<>();
                for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                    log.info("[{}-{}]", i.getIdIndicador(), i.getNombre());
                    ExcelIndicadorDto modelExcelIndicadorDto = new ExcelIndicadorDto();
                    modelExcelIndicadorDto.setIdIndicador(i.getIdIndicador());
                    modelExcelIndicadorDto.setNombreIndicador(i.getNombre());
                    modelExcelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
                    modelExcelIndicadorDto.setValorMeta(i.getValorMeta());
                    modelExcelIndicadorDto.setPeso(i.getPeso());

                    List<Tarea> listTarea = tareaRepository.getTareasByIdIndicador(i.getIdIndicador());
                    log.info("cantidad de tareas por indicador [{}]", listTarea.size());

                    List<ExcelTareaDto> listExcelTareaDto = new ArrayList<>();
                    for (Tarea t : listTarea) {
                        log.info("[{}]", t.getNombre());
                        ExcelTareaDto modelExcelTareaDto = new ExcelTareaDto();
                        modelExcelTareaDto.setIdTarea(t.getIdTarea());
                        modelExcelTareaDto.setNombre(t.getNombre());
                        modelExcelTareaDto.setPlazo(t.getPlazo());

                        modelExcelTareaDto.setFechaCreacion(t.getFechaCreacion());
                        modelExcelTareaDto.setMotivoRechazo(t.getMotivoRechazo());
                        modelExcelTareaDto.setEvidenciaDescripcion(t.getEvidenciaDescripcion());
                        modelExcelTareaDto.setEvidenciaFechaRegistro(t.getEvidenciaFechaRegistro());
                        modelExcelTareaDto.setEvidenciaExtensionFile(t.getEvidenciaExtensionFile());

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

}
