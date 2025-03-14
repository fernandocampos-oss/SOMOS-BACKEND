package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.GdrParametro;
import pe.gob.essalud.apps.repository.miessalud.GdrParametroRepository;
import pe.gob.essalud.apps.service.ParametroService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParametroServiceImpl implements ParametroService {

    private final GdrParametroRepository gdrParametroRepository;

    @Override
    public GdrParametro obtenerParametros() {
        return gdrParametroRepository.findById(1).orElse(new GdrParametro());
    }

    @Override
    public void actualizarParametros(Integer id, GdrParametro request) {
        GdrParametro parametro = gdrParametroRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El parámetro no se encuentra"));
        parametro.setFechaLimitePlanificacion(request.getFechaLimitePlanificacion().withHour(0).withMinute(0).withSecond(0).withNano(0));
        parametro.setFechaLimiteSeguimiento(request.getFechaLimiteSeguimiento().withHour(0).withMinute(0).withSecond(0).withNano(0));
        parametro.setFechaLimiteEvaluacion(request.getFechaLimiteEvaluacion().withHour(0).withMinute(0).withSecond(0).withNano(0));
        gdrParametroRepository.save(parametro);
    }

}
