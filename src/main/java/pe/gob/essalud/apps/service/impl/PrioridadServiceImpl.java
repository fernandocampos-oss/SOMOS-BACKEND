package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainIndicadorDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainPrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainTareaDto;
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

            //prioridades
            List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(e.getIntegrante().getIdUsuario());
            log.info("cantidad de prioridades [{}]", prioridades.size());
            List<MainPrioridadDto> listPrioridadDto = new ArrayList<>();
            for (Prioridad p : prioridades) {
                log.info("peso-actividad [{}-{}]", p.getPeso(), p.getActividad().getDescripcion());
                MainPrioridadDto modelPrioridadDto = new MainPrioridadDto();
                modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
                modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());
                modelPrioridadDto.setPeso(p.getPeso());

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

                    List<Tarea> listTarea = tareaRepository.getTareasByIdIndicador(i.getIdIndicador());
                    log.info("cantidad de tareas por indicador [{}]", listTarea.size());

                    List<MainTareaDto> listTareaDto = new ArrayList<>();
                    for (Tarea t : listTarea) {
                        log.info("[{}]", t.getNombre());
                        MainTareaDto modelTareaDto = new MainTareaDto();
                        modelTareaDto.setNombre(t.getNombre());
                        modelTareaDto.setPlazo(t.getPlazo());
                        listTareaDto.add(modelTareaDto);
                    }
                    modelIndicadorDto.setListTarea(listTareaDto); //add tareas
                    listIndicadorDto.add(modelIndicadorDto);
                }
                modelPrioridadDto.setListIndicador(listIndicadorDto); //add indicadores
                listPrioridadDto.add(modelPrioridadDto);
            }
            modelMainDto.setListPrioridad(listPrioridadDto); //add prioridad
            listMainDto.add(modelMainDto);
        }
        return listMainDto;
    }

    @Override
    public List<Indicador> getAllIndicadorOrganizar() {
        return prioridadRepository.getAllIndicadorOrganizar();
    }

//    @Override
//    public int actualizarPrioridad(Integer idActividad, Integer idPrioridad) {
//        log.info("asignar prioridad [{}-{}]", idActividad, idPrioridad);
//        return prioridadRepository.actualizarPrioridad(idActividad, idPrioridad);
//    }

    @Override
    public void actualizarPrioridadEnListaIndicadores(PrioridadDto prioridadDto) {
        log.info(">>>111 [{}]", prioridadDto.getActividad().getIdActividad());
        log.info(">>>222 [{}]", prioridadDto.getListIdIndicador());

        Prioridad model = new Prioridad();
        model.setActividad(prioridadDto.getActividad());

        LocalDate fechaActualTmp = LocalDate.now();
        int anioRegistro = fechaActualTmp.getYear();
        model.setAnioRegistro(anioRegistro);

        Prioridad result = prioridadRepository.save(model);
        log.info(">>>result [{}]", result);

        prioridadRepository.actualizarPrioridadEnListaIndicadores(result.getIdPrioridad(), prioridadDto.getListIdIndicador());

    }

    @Override
    public int asignarPesoPrioridad(int peso, int idPrioridad) {
        return prioridadRepository.asignarPesoPrioridad(peso, idPrioridad);
    }

    @Override
    public List<Actividad> getAllActividades() {
        return actividadRepository.findAll();
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
//
////    @Override
////    public List<IndicadorUsuario> getlistIndicadoresByIdUsuario(Number idUsuario) {
////        return requerimientoUsuarioRepository.getlistIndicadoresByIdUsuario(idUsuario);
////    }
//
//    @Override
//    public int finalizarTareaAdministrador(Number idRequerimientoUsuario) {
//        return indicadorUsuarioRepository.finalizarTareaAdministrador(LocalDateTime.now(ZoneId.of("America/Lima")), idRequerimientoUsuario);
//    }
//
//    @Override
//    public List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(Number anioRegistro) {
//        return indicadorUsuarioRepository.getAllRequerimientoUsuarioPorAnio(anioRegistro);
//    }

}
