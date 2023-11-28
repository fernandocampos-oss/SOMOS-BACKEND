package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.common.constants.gestionrendimiento.EstadoEvidenciaConstant;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.*;
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
        LocalDate fechaActualTmp = LocalDate.now();
        int anioRegistro = fechaActualTmp.getYear();

        Prioridad prioridad = new Prioridad();
        prioridad.setAnio(anioRegistro);
        prioridad.setActividad(requestDto.getActividad());
        Prioridad prioridadGuardado = prioridadRepository.save(prioridad);

        Indicador model = requestDto.getIndicador();
        model.setAnio(anioRegistro);
        model.setEstado(true);
        model.setUsuarioCreacion(authService.getIdUserSession());
        model.setVotante(requestDto.getVotante());
        model.setPrioridad(prioridadGuardado);
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
        LocalDate fechaActual = LocalDate.now();
        int anioActual = fechaActual.getYear();

        Votante votante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(anioActual, votante.getIdVotante());

        List<PendienteDto> listPrioridadDto = new ArrayList<>();
        for (Prioridad p : prioridades) {
            PendienteDto modelPrioridadDto = new PendienteDto();
            modelPrioridadDto.setIdPrioridad(p.getIdPrioridad());
            modelPrioridadDto.setPrioridadNombre(p.getActividad().getDescripcion());

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
    public List<TipoValorMeta> getAllTipoValorMeta() {
        return tipoValorMetaRepository.findAll();
    }

    @Override
    public void modificarIndicador(Integer idIndicador, Indicador request) {
        indicadorRepository.modificarIndicador(request.getDescripcion(),
                request.getTipoValorMeta().getIdTipoValorMeta(),
                request.getValorMeta(),
                LocalDateTime.now(ZoneId.of("America/Lima")),
                authService.getIdUserSession(),
                idIndicador);
    }

    @Override
    public int asignarPesoIndicador(int peso, int idIndicador) {
        return indicadorRepository.asignarPesoIndicador(peso, idIndicador);
    }

    @Override
    public ExcelTrabajadorDto generarExcelTrabajador() {
        LocalDate fechaActual = LocalDate.now();
        int anioActual = fechaActual.getYear();

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

        List<Prioridad> prioridades = prioridadRepository.getListIdPrioridadesByTrabajador(anioActual, votanteTrabajador.getIdVotante());

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

}
