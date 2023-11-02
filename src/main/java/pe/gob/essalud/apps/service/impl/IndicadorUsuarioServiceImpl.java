package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.IndicadorUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoValorMeta;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EquipoRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.IndicadorUsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.TipoIngresoRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.TipoValorMetaRepository;
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

    private final IndicadorUsuarioRepository requerimientoUsuarioRepository;
    private final AuthService authService;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;
    private final EquipoRepository liderEquipoRepository;
    private final TipoIngresoRepository tipoIngresoRepository;
    private final TipoValorMetaRepository tipoValorMetaRepository;

    @Override
    public List<IndicadorUsuario> listar() {
        return null;
    }

    @Override
    public IndicadorUsuario registrar(IndicadorUsuario obj) {
        return null;
    }

    @Override
    public List<IndicadorUsuario> listarRequerimientosIntegrantesPrincipal() {
        int idLider = authService.getIdUserSession();
        List<Equipo> listIntegrantes =  liderEquipoRepository.getListTrabajadoresByIdUsuarioJefe(idLider);
        log.info(">>>>> [{}]", listIntegrantes.size());

        List<IndicadorUsuario> getRequerimientosAllIntegrantesPorLider = new ArrayList<>();

        List<Long> arrayIdIntegrantes = new ArrayList<Long>();
        for (Equipo integrante : listIntegrantes) {
            log.info(">>>>>array [{}]", integrante.getIntegrante().getIdUsuario());
            arrayIdIntegrantes.add(Long.valueOf(integrante.getIntegrante().getIdUsuario()));
		}
        getRequerimientosAllIntegrantesPorLider = requerimientoUsuarioRepository.listarRequerimientosIntegrantesPrincipal(arrayIdIntegrantes);
        return getRequerimientosAllIntegrantesPorLider;
    }

    @Override
    public List<IndicadorUsuario> listarRequerimientosPendientesPorUsuario() {
        int idUsuario = authService.getIdUserSession();
        return requerimientoUsuarioRepository.listarRequerimientosPendientesPorUsuario(idUsuario);
    }

    @Override
    public List<IndicadorUsuario> listarRequerimientosFinalizadoPorUsuario() {
        int idUsuario = authService.getIdUserSession();
        return requerimientoUsuarioRepository.listarRequerimientosFinalizadoPorUsuario(idUsuario);
    }

//    @Override
//    public List<RequerimientoUsuario> listarRequerimientosRechazadoPorUsuario() {
//        int idUsuario = authService.getIdUserSession();
//        return requerimientoUsuarioRepository.listarRequerimientosRechazadoPorUsuario(idUsuario);
//    }

    @Override
    public int aprobarRequerimiento(Number estado, Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.aprobarRequerimiento(estado, idRequerimientoUsuario);
    }

//    @Override
//    public int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario) {
//        return requerimientoUsuarioRepository.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
//    }

//    @Override
//    public int derivarRequerimiento(Number estado, String motivo, String codUnidadReceptor, Number idRequerimientoUsuario) {
//        return requerimientoUsuarioRepository.derivarRequerimiento(estado, motivo, codUnidadReceptor, idRequerimientoUsuario);
//    }

//    @Override
//    public List<UnidadOrganizativa> listarUnidad() {
//        return unidadOrganizativaRepository.findAll();
//    }

    @Override
    public List<IndicadorUsuario> listarRequerimientosPorPersonal(Number idUsuario) {
        return requerimientoUsuarioRepository.listarRequerimientosPorPersonal(idUsuario);
    }
//
//    @Override
//    public List<PersonalDTO> listarPersonalPorRed() {
//        String codRed =  authService.getCodRedSession();
//        return requerimientoUsuarioRepository.listarPersonalPorRed(codRed);
//    }

    @Override
    public int finalizarTareaAdministrador(Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.finalizarTareaAdministrador(LocalDateTime.now(ZoneId.of("America/Lima")), idRequerimientoUsuario);
    }

    @Override
    public List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(Number anioRegistro) {
        return requerimientoUsuarioRepository.getAllRequerimientoUsuarioPorAnio(anioRegistro);
    }

    @Override
    public List<TipoIngreso> getAllTipoIngreso() {
        return tipoIngresoRepository.findAll();
    }

    @Override
    public List<TipoValorMeta> getAllTipoValorMeta() {
        return tipoValorMetaRepository.findAll();
    }


}
