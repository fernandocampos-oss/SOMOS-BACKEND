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
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.IndicadorService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Override
    @Transactional
    public void registrarIndicador(IndicadorRequestDto requestDto) {
        Prioridad prioridad = new Prioridad();
        prioridad.setAnio(DateUtil.getYearCurrent());
        prioridad.setActividad(requestDto.getActividad());
        System.out.println("Flack: " + requestDto.getIndicador().getFlDesPrioridad());
        if(requestDto.getIndicador().getFlDesPrioridad().equalsIgnoreCase("1")) {
        	System.out.println("Reemplaza valor.");
        	prioridad.setDescripcion(requestDto.getIndicador().getDesPrioridad());
        }
        Prioridad prioridadGuardado = prioridadRepository.save(prioridad);

        Indicador model = requestDto.getIndicador();
        model.setAnio(DateUtil.getYearCurrent());
        model.setEstado(true);
        model.setUsuarioCreacion(authService.getIdUserSession());
        model.setVotante(requestDto.getVotante());
        model.setPrioridad(prioridadGuardado);
        model.setCodRed(authService.getCodRedSession());
        model.setCodUnidad(authService.getCodUnidadSession());
        
        Indicador indicadorGuardado = indicadorRepository.save(model);

        if (!requestDto.getListEvidencia().isEmpty()) {
            for (Evidencia i : requestDto.getListEvidencia()) {
                i.setIndicador(indicadorGuardado);
                i.setUsuarioCreacion(authService.getIdUserSession());

                EstadoEvidencia estadoEvidencia = new EstadoEvidencia();
                estadoEvidencia.setIdEstadoEvidencia(EstadoEvidenciaConstant.REGISTRADO);
                i.setEstadoEvidencia(estadoEvidencia);

                i.setEstado(true);
                evidenciaRepository.save(i);
            }
        }
    }

    @Override
    public List<PendienteDto> listPendientesTrabajadorByUser() {

        Votante votante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());

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
            modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());
            modelPrioridadDto.setIdActividad(p.getActividad().getIdActividad());
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            int porcentajeTotal = 0;

            List<Indicador> indicadoresPorTrabajadorYPrioridad = indicadorRepository.getListIndicadoresByUsuarioAndPrioridad(idVotante, p.getIdPrioridad());

            List<PendienteIndicadorDto> listIndicadorDto = new ArrayList<>();
            for (Indicador i : indicadoresPorTrabajadorYPrioridad) {
                log.info("[{}-{}]", i.getIdIndicador(), i.getDescripcion());
                PendienteIndicadorDto modelIndicadorDto = new PendienteIndicadorDto();
                modelIndicadorDto.setIdIndicador(i.getIdIndicador());
                modelIndicadorDto.setNombreIndicador(i.getDescripcion());
                modelIndicadorDto.setIdTipoValorMeta(i.getTipoValorMeta().getIdTipoValorMeta());
                modelIndicadorDto.setCodTipoValorMeta(i.getTipoValorMeta().getCodigo());
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
        if (votanteTrabajador.getIdSegmento() == 1) {
            mainDto.setEvaluadoSegmento("DIRECTIVO");
        }
        if (votanteTrabajador.getIdSegmento() == 3) {
            mainDto.setEvaluadoSegmento("EJECUTOR");
        }
        Equipo JefeEquipo = equipoRepository.getJefeByIdIntegrante(votanteTrabajador.getIdVotante());
        EvaluadorResponseDto jefe = prioridadRepository.findUsuarioById(JefeEquipo.getJefe().getIdUsuario());
        UnidadOrganizativa unidadJefe = prioridadRepository.getUnidadByCod(jefe.getUnidad());
        mainDto.setEvaluadorCodUnidad(unidadJefe.getDescripcion());
        mainDto.setEvaluadorNombreCompleto(JefeEquipo.getJefe().getApellidos() + " " + JefeEquipo.getJefe().getNombres());
        mainDto.setEvaluadorPuesto(jefe.getPuesto());
        if (JefeEquipo.getJefe().getIdSegmento() == 1) {
            mainDto.setEvaluadorSegmento("DIRECTIVO");
        }
        mainDto.setEvaluadorNumeroDocumento(jefe.getNumeroDocumento());

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(DateUtil.getYearCurrent(), votanteTrabajador.getIdVotante());

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setFechaAsignacionPrioridad(p.getFechaAsignacion());
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());

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

}
