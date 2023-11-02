package pe.gob.essalud.apps.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.IndicadorRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.IndicadorUsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.IndicadorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicadorServiceImpl implements IndicadorService {

    private final IndicadorRepository indicadorRepository;
    private final IndicadorUsuarioRepository requerimientoUsuarioRepository;
    private final AuthService authService;

    @Override
    public List<Indicador> listar() {
        return null;
    }

    @Override
    public Indicador registrar(Indicador model) {
        return null;
    }

    @Override
    public Indicador registrarIndicador(Indicador model) {
        log.info(">>>>model [{}]", model);
        if (model != null) {
            model.setEstado(true);
            model.setUsuarioCreacion(authService.getIdUserSession());
        }
        Indicador result = indicadorRepository.save(model);
        if(result != null) {
            int idUsuario = authService.getIdUserSession();
            String codRed =  authService.getCodRedSession();
            String codUnidad= authService.getCodUnidadSession();

            LocalDate fechaActualTmp = LocalDate.now();
            int anioRegistroIndicador = fechaActualTmp.getYear();
            requerimientoUsuarioRepository.registrarIndicadorUsuario(result.getIdIndicador(), codRed, codUnidad, idUsuario, 1, LocalDateTime.now(ZoneId.of("America/Lima")), anioRegistroIndicador) ;
        }
        return result;
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

}

