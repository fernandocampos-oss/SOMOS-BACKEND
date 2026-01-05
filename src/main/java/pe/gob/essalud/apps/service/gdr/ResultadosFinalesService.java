package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ResultadosFinales;
import pe.gob.essalud.apps.repository.gdr.ResultadosFinalesRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultadosFinalesService {

    private final ResultadosFinalesRepository resultadosFinalesRepository;

    @Transactional("gdrTransactionManager")
    public ResultadosFinales guardarOActualizar(Long idVotante, Integer anio, String rendimientoDistinguido, 
                                                 String accionesCapacitacion, String otrasAcciones, LocalDate fechaReunion) {
        Optional<ResultadosFinales> existente = resultadosFinalesRepository.findByIdVotanteAndAnio(idVotante, anio);

        ResultadosFinales resultados;
        if (existente.isPresent()) {
            resultados = existente.get();
            log.info("Actualizando resultados finales para votante {} año {}", idVotante, anio);
        } else {
            resultados = new ResultadosFinales();
            resultados.setIdVotante(idVotante);
            resultados.setAnio(anio);
            log.info("Creando nuevos resultados finales para votante {} año {}", idVotante, anio);
        }

        resultados.setRendimientoDistinguido(rendimientoDistinguido);
        resultados.setAccionesCapacitacion(accionesCapacitacion);
        resultados.setOtrasAcciones(otrasAcciones);
        resultados.setFechaReunion(fechaReunion);

        return resultadosFinalesRepository.save(resultados);
    }

    @Transactional(value = "gdrTransactionManager", readOnly = true)
    public Optional<ResultadosFinales> obtenerPorVotanteYAnio(Long idVotante, Integer anio) {
        return resultadosFinalesRepository.findByIdVotanteAndAnio(idVotante, anio);
    }

    @Transactional(value = "gdrTransactionManager", readOnly = true)
    public Map<Long, ResultadosFinales> obtenerMultiples(List<Long> idsVotantes, Integer anio) {
        List<ResultadosFinales> lista = resultadosFinalesRepository.findByIdVotanteInAndAnio(idsVotantes, anio);
        Map<Long, ResultadosFinales> mapa = new HashMap<>();
        for (ResultadosFinales rf : lista) {
            mapa.put(rf.getIdVotante(), rf);
        }
        return mapa;
    }

    @Transactional("gdrTransactionManager")
    public void eliminar(Long idVotante, Integer anio) {
        resultadosFinalesRepository.deleteByIdVotanteAndAnio(idVotante, anio);
        log.info("Eliminados resultados finales para votante {} año {}", idVotante, anio);
    }
}
