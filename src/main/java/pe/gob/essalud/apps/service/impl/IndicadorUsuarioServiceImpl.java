package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.GestionIndicadoresTrabajadorDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.IndicadorUsuarioService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicadorUsuarioServiceImpl implements IndicadorUsuarioService {

    private final IndicadorUsuarioRepository indicadorUsuarioRepository;
    private final AuthService authService;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;
    private final EquipoRepository liderEquipoRepository;
    private final TipoIngresoRepository tipoIngresoRepository;
    private final TipoValorMetaRepository tipoValorMetaRepository;
    private final ActividadRepository actividadRepository;

    @Override
    public List<GestionIndicadoresTrabajadorDto> listarTrabajadoresIndicadoresJefePrincipal() {
        int idUsuario = authService.getIdUserSession();
        List<Equipo> listTrabajadores = liderEquipoRepository.getListTrabajadoresByIdUsuarioJefe(idUsuario);
        log.info("cantidad de trabajadores [{}]", listTrabajadores.size());

        List<GestionIndicadoresTrabajadorDto> listDtoPrincipal = new ArrayList<>();
        for (Equipo trabajador : listTrabajadores) {
            GestionIndicadoresTrabajadorDto dtoModel = new GestionIndicadoresTrabajadorDto();
            dtoModel.setTrabajadorNombre(trabajador.getIntegrante().getNombres());
            dtoModel.setTrabajadorApellido(trabajador.getIntegrante().getApellidos());

            List<IndicadorUsuario> listIndicadoresPorTrabajador = indicadorUsuarioRepository.listarIndicadoresPendientesPorUsuario(trabajador.getIntegrante().getIdUsuario());
            log.info("cantidadIndicadoresPorTrabajador [{}]", listIndicadoresPorTrabajador.size());

            dtoModel.setListIndicador(listIndicadoresPorTrabajador);

            listDtoPrincipal.add(dtoModel);
        }
        return listDtoPrincipal;
    }

    @Override
    public List<IndicadorUsuario> listarIndicadoresPendientesPorUsuario() {
        return indicadorUsuarioRepository.listarIndicadoresPendientesPorUsuario(authService.getIdUserSession());
    }

    @Override
    public List<IndicadorUsuario> listarIndicadoresFinalizadoPorUsuario() {
        return indicadorUsuarioRepository.listarIndicadoresFinalizadoPorUsuario(authService.getIdUserSession());
    }

//    @Override
//    public int aprobarIndicador(Number estado, Number idIndicadorUsuario) {
//        return requerimientoUsuarioRepository.aprobarIndicador(estado, idIndicadorUsuario);
//    }

//    @Override
//    public int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario) {
//        return requerimientoUsuarioRepository.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
//    }

//    @Override
//    public List<IndicadorUsuario> getlistIndicadoresByIdUsuario(Number idUsuario) {
//        return requerimientoUsuarioRepository.getlistIndicadoresByIdUsuario(idUsuario);
//    }

    @Override
    public int finalizarTareaAdministrador(Number idRequerimientoUsuario) {
        return indicadorUsuarioRepository.finalizarTareaAdministrador(LocalDateTime.now(ZoneId.of("America/Lima")), idRequerimientoUsuario);
    }

    @Override
    public List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(Number anioRegistro) {
        return indicadorUsuarioRepository.getAllRequerimientoUsuarioPorAnio(anioRegistro);
    }

    @Override
    public List<TipoIngreso> getAllTipoIngreso() {
        return tipoIngresoRepository.findAll();
    }

    @Override
    public List<TipoValorMeta> getAllTipoValorMeta() {
        return tipoValorMetaRepository.findAll();
    }

    @Override
    public List<Actividad> getAllActividades() {
        return actividadRepository.findAll();
    }

}
