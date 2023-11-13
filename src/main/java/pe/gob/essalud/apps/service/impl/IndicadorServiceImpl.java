package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.common.constants.gestionrendimiento.EstadoIndicadorConstant;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.IndicadorService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicadorServiceImpl implements IndicadorService {

    private final IndicadorRepository indicadorRepository;
    private final AuthService authService;
    private final TipoValorMetaRepository tipoValorMetaRepository;
    private final TipoIngresoRepository tipoIngresoRepository;

    @Override
    public Indicador registrarIndicador(Indicador model) {
        if (model != null) {
            model.setEsAsignado(false);
            model.setEstado(true);
            model.setUsuarioCreacion(authService.getIdUserSession());

            LocalDate fechaActualTmp = LocalDate.now();
            int anioRegistro = fechaActualTmp.getYear();
            model.setAnioRegistro(anioRegistro);

            Usuario usuario = new Usuario();
            usuario.setIdUsuario(authService.getIdUserSession());
            model.setUsuario(usuario);

            EstadoIndicador estadoIndicador = new EstadoIndicador();
            estadoIndicador.setIdEstadoIndicador(EstadoIndicadorConstant.PENDIENTE_APROBACION);
            model.setEstadoIndicador(estadoIndicador);
        }
        return indicadorRepository.save(model);
    }


//    public Indicador registrarIndicador(Indicador model) {
//        log.info("indicador [{}]", model);
//        if (model != null) {
//            model.setEstado(true);
//            model.setUsuarioCreacion(authService.getIdUserSession());
//        }
//        Indicador result = indicadorRepository.save(model);
//        if(result != null) {
//            int idUsuario = authService.getIdUserSession();
//            String codRed =  authService.getCodRedSession();
//            String codUnidad= authService.getCodUnidadSession();
//
//            LocalDate fechaActualTmp = LocalDate.now();
//            int anioRegistroIndicador = fechaActualTmp.getYear();
//            requerimientoUsuarioRepository.registrarIndicadorUsuario(result.getIdIndicador(), codRed, codUnidad, idUsuario, 1, LocalDateTime.now(ZoneId.of("America/Lima")), anioRegistroIndicador, 0) ;
//        }
//        return result;
//    }

    @Override
    public List<Indicador> getListIndicadoresPendientesByUser() {
        return indicadorRepository.getListIndicadoresPendientesByUser(authService.getIdUserSession());
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
    public void modificarIndicador(Integer idIndicador, Indicador request) {
        indicadorRepository.modificarIndicador(request.getNombre(),
                request.getDescripcion(),
                request.getTipoIngreso().getIdTipoIngreso(),
                request.getTipoValorMeta().getIdTipoValorMeta(),
                request.getValorMeta(),
                LocalDateTime.now(ZoneId.of("America/Lima")),
                authService.getIdUserSession(),
                idIndicador);
    }


    @Override
    public List<Indicador> getListIndicadoresFinalizadoByUser() {
        return indicadorRepository.getListIndicadoresFinalizadoByUser(authService.getIdUserSession());
    }

}
