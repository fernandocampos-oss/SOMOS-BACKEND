package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.DashboardAvanceRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.DashboardAvanceDto;
import pe.gob.essalud.apps.model.gdr.ResultadosFinales;
import pe.gob.essalud.apps.model.gdr.ValorAlcanzadoPrioridad;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Evidencia;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EvidenciaRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.IndicadorRepository;
import pe.gob.essalud.apps.repository.miessalud.VotanteRepository;
import pe.gob.essalud.apps.repository.gdr.ResultadosFinalesRepository;
import pe.gob.essalud.apps.repository.gdr.ValorAlcanzadoPrioridadRepository;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardAvanceService {

    private final IndicadorRepository indicadorRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final VotanteRepository votanteRepository;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ResultadosFinalesRepository resultadosFinalesRepository;
    private final ValorAlcanzadoPrioridadRepository valorAlcanzadoPrioridadRepository;

    @PersistenceContext(unitName = "persistenceUnit1")
    private EntityManager entityManager;
    
    @PersistenceContext(unitName = "gdrEntityManagerFactory")
    private EntityManager gdrEntityManager;

    @Transactional(readOnly = true)
    public List<DashboardAvanceDto> obtenerDashboard(DashboardAvanceRequestDto request) {
        log.info("Generando dashboard - tipo: {}, anio: {}", request.getTipoAgrupacion(), request.getAnio());

        if (request.getAnio() == null || request.getAnio().isEmpty()) {
            request.setAnio(String.valueOf(java.time.Year.now().getValue()));
        }
        if (request.getTipoAgrupacion() == null || request.getTipoAgrupacion().isEmpty()) {
            request.setTipoAgrupacion("PERSONA");
        }

        List<Object[]> datos = obtenerDatosBase(request);
        if (datos == null || datos.isEmpty()) {
            log.info("No se encontraron datos para los filtros especificados");
            return new ArrayList<>();
        }

        int anio = Integer.parseInt(request.getAnio());

        // ── Extraer ids de votantes ─────────────────────────────────────────
        List<Integer> idsVotantes = datos.stream()
                .map(row -> ((Number) row[0]).intValue())
                .distinct()
                .collect(Collectors.toList());

        // ── Query batch 1: todos los indicadores de todos los votantes ──────
        Map<Integer, List<Indicador>> indicadoresPorVotante = obtenerIndicadoresBatch(idsVotantes, anio);

        // ── Query batch 2: todas las evidencias de todos esos indicadores ───
        List<Integer> idsIndicadores = indicadoresPorVotante.values().stream()
                .flatMap(List::stream)
                .map(Indicador::getIdIndicador)
                .collect(Collectors.toList());
        Map<Integer, List<Evidencia>> evidenciasPorIndicador = obtenerEvidenciasBatch(idsIndicadores);

        // ── Query batch 3: fases planeación (reunion) para todos los votantes
        Map<Integer, Integer> fasePlaneacionPorVotante = obtenerFasePlaneacionBatch(idsVotantes, String.valueOf(anio));

        // ── Calcular métricas por persona ────────────────────────────────────
        List<DashboardAvanceDto> personasDtos = calcularMetricasPorPersona(
                datos, indicadoresPorVotante, evidenciasPorIndicador, fasePlaneacionPorVotante, anio);

        log.info("Dashboard generado: {} personas procesadas", personasDtos.size());

        String tipo = request.getTipoAgrupacion().toUpperCase();
        switch (tipo) {
            case "UNIDAD":  return agruparPorUnidad(personasDtos);
            case "ORGANO":  return agruparPorOrgano(personasDtos);
            default:        return personasDtos;
        }
    }

    private List<Object[]> obtenerDatosBase(DashboardAvanceRequestDto request) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT v.id_votante, v.numero_documento, v.nombres, v.apellidos, ");
            sql.append("v.id_usuario, i.cod_unidad, i.cod_red ");
            sql.append("FROM indicador i ");
            sql.append("INNER JOIN votante v ON i.id_votante = v.id_votante ");
            sql.append("WHERE i.anio = :anio AND i.estado = true ");
            
            if (request.getCodUnidad() != null && !request.getCodUnidad().isEmpty()) {
                sql.append("AND i.cod_unidad = :codUnidad ");
            }
            
            if (request.getListCodRed() != null && !request.getListCodRed().isEmpty() && !Boolean.TRUE.equals(request.getAllRed())) {
                sql.append("AND i.cod_red IN (:listCodRed) ");
            }

            log.info("SQL Dashboard: {}", sql.toString());
            Query query = entityManager.createNativeQuery(sql.toString());
            query.setParameter("anio", Integer.parseInt(request.getAnio()));
            
            if (request.getCodUnidad() != null && !request.getCodUnidad().isEmpty()) {
                query.setParameter("codUnidad", request.getCodUnidad());
            }
            
            if (request.getListCodRed() != null && !request.getListCodRed().isEmpty() && !Boolean.TRUE.equals(request.getAllRed())) {
                query.setParameter("listCodRed", request.getListCodRed());
            }

            List<Object[]> result = query.getResultList();
            log.info("Datos encontrados: {} registros", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("Error en obtenerDatosBase: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // ── Batch queries ────────────────────────────────────────────────────────

    private Map<Integer, List<Indicador>> obtenerIndicadoresBatch(List<Integer> idsVotantes, int anio) {
        if (idsVotantes.isEmpty()) return new HashMap<>();
        try {
            String sql = "SELECT * FROM indicador WHERE id_votante IN (:ids) AND anio = :anio AND estado = true";
            Query query = entityManager.createNativeQuery(sql, Indicador.class);
            query.setParameter("ids", idsVotantes);
            query.setParameter("anio", anio);
            List<Indicador> todos = query.getResultList();
            return todos.stream().collect(Collectors.groupingBy(i -> i.getVotante().getIdVotante()));
        } catch (Exception e) {
            log.error("Error en obtenerIndicadoresBatch: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<Integer, List<Evidencia>> obtenerEvidenciasBatch(List<Integer> idsIndicadores) {
        if (idsIndicadores.isEmpty()) return new HashMap<>();
        try {
            // Procesar en lotes de 1000 para evitar límite de IN clause
            Map<Integer, List<Evidencia>> resultado = new HashMap<>();
            int batchSize = 1000;
            for (int i = 0; i < idsIndicadores.size(); i += batchSize) {
                List<Integer> lote = idsIndicadores.subList(i, Math.min(i + batchSize, idsIndicadores.size()));
                String sql = "SELECT * FROM evidencia WHERE id_indicador IN (:ids) AND estado = true ORDER BY id_evidencia ASC";
                Query query = entityManager.createNativeQuery(sql, Evidencia.class);
                query.setParameter("ids", lote);
                List<Evidencia> evidencias = query.getResultList();
                evidencias.forEach(ev -> resultado
                        .computeIfAbsent(ev.getIndicador().getIdIndicador(), k -> new ArrayList<>())
                        .add(ev));
            }
            return resultado;
        } catch (Exception e) {
            log.error("Error en obtenerEvidenciasBatch: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<Integer, Integer> obtenerFasePlaneacionBatch(List<Integer> idsVotantes, String periodo) {
        if (idsVotantes.isEmpty()) return new HashMap<>();
        try {
            String sql = "SELECT id_votante_evaluado, confirmado FROM reunion_establecimiento_metas " +
                         "WHERE id_votante_evaluado IN (:ids) AND periodo = :periodo";
            Query query = gdrEntityManager.createNativeQuery(sql);
            query.setParameter("ids", idsVotantes.stream().map(Long::valueOf).collect(Collectors.toList()));
            query.setParameter("periodo", periodo);
            List<Object[]> rows = query.getResultList();
            Map<Integer, Integer> resultado = new HashMap<>();
            for (Object[] row : rows) {
                Integer idVotante = ((Number) row[0]).intValue();
                boolean confirmado = row[1] instanceof Boolean ? (Boolean) row[1] : ((Number) row[1]).intValue() == 1;
                resultado.put(idVotante, confirmado ? 1 : 0);
            }
            return resultado;
        } catch (Exception e) {
            log.warn("Error en obtenerFasePlaneacionBatch: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // ── Cálculo de métricas (usa datos ya cargados en memoria) ──────────────

    private List<DashboardAvanceDto> calcularMetricasPorPersona(
            List<Object[]> datos,
            Map<Integer, List<Indicador>> indicadoresPorVotante,
            Map<Integer, List<Evidencia>> evidenciasPorIndicador,
            Map<Integer, Integer> fasePlaneacionPorVotante,
            int anio) {

        List<DashboardAvanceDto> resultado = new ArrayList<>();

        // Batch: resultados finales
        List<Integer> idsVotantes = datos.stream()
                .map(row -> ((Number) row[0]).intValue()).collect(Collectors.toList());
        Map<Integer, ResultadosFinales> resultadosFinalMap = new HashMap<>();
        try {
            resultadosFinalesRepository.findByIdVotanteInAndAnio(
                    idsVotantes.stream().map(Long::valueOf).collect(Collectors.toList()), anio)
                    .forEach(rf -> resultadosFinalMap.put(rf.getIdVotante().intValue(), rf));
        } catch (Exception e) {
            log.warn("Error cargando resultados finales batch: {}", e.getMessage());
        }

        for (Object[] row : datos) {
            try {
                Integer idVotante = ((Number) row[0]).intValue();
                String numeroDocumento = row[1] != null ? (String) row[1] : "";
                String nombres  = row[2] != null ? (String) row[2] : "";
                String apellidos = row[3] != null ? (String) row[3] : "";
                String codUnidad = row[5] != null ? (String) row[5] : "";

                DashboardAvanceDto dto = new DashboardAvanceDto();
                dto.setCodigo(numeroDocumento);
                dto.setNombre((apellidos + " " + nombres).trim());
                dto.setTipo("PERSONA");
                dto.setTotalTrabajadores(1);
                dto.setCodUnidad(codUnidad);

                List<Indicador> indicadores = indicadoresPorVotante.getOrDefault(idVotante, new ArrayList<>());
                dto.setTotalIndicadores(indicadores.size());

                int totalEvidenciasCiclo = 0, evidenciasCicloSubidas = 0;
                int totalEvidenciasFinales = 0, evidenciasFinalesSubidas = 0;
                int totalEvidencias = 0, evidenciasSubidas = 0;
                int indicadoresCompletados = 0;
                boolean faseSeguimientoOk = !indicadores.isEmpty();

                for (Indicador ind : indicadores) {
                    List<Evidencia> evidencias = evidenciasPorIndicador.getOrDefault(ind.getIdIndicador(), new ArrayList<>());
                    int subidas = 0;

                    for (Evidencia ev : evidencias) {
                        totalEvidencias++;
                        boolean tieneArchivo = ev.getSustentoRutaFile() != null && !ev.getSustentoRutaFile().isBlank();
                        if (tieneArchivo) { evidenciasSubidas++; subidas++; }

                        String desc = ev.getDescripcion() != null ? ev.getDescripcion().toUpperCase().trim() : "";
                        if ("SUSTENTO FINAL".equals(desc)) {
                            totalEvidenciasFinales++;
                            if (tieneArchivo) evidenciasFinalesSubidas++;
                        } else {
                            totalEvidenciasCiclo++;
                            if (tieneArchivo) evidenciasCicloSubidas++;
                            // Fase seguimiento: solo evidencias iniciales deben tener comentario
                            if (faseSeguimientoOk) {
                                String comentario = ev.getComentario();
                                if (comentario == null || comentario.isBlank()) faseSeguimientoOk = false;
                            }
                        }
                    }

                    if (!evidencias.isEmpty() && subidas == evidencias.size()) indicadoresCompletados++;
                }

                dto.setIndicadoresCompletados(indicadoresCompletados);
                dto.setEvidenciasIniciales(totalEvidenciasCiclo);
                dto.setEvidenciasSeguimiento(0);
                dto.setEvidenciasFinales(totalEvidenciasFinales);
                dto.setTotalEvidencias(totalEvidencias);
                dto.setEvidenciasSubidas(evidenciasSubidas);
                dto.setPorcentajeAvance(totalEvidencias > 0
                        ? roundDouble((evidenciasSubidas * 100.0) / totalEvidencias) : 0.0);

                // Resultado final
                ResultadosFinales rf = resultadosFinalMap.get(idVotante);
                if (rf != null) {
                    dto.setConCalificacion(1);
                    dto.setSinCalificacion(0);
                    dto.setDistinguidos("SI".equalsIgnoreCase(rf.getRendimientoDistinguido()) ? 1 : 0);
                } else {
                    dto.setConCalificacion(0);
                    dto.setSinCalificacion(1);
                    dto.setDistinguidos(0);
                }

                // Fases
                dto.setFasePlaneacion(fasePlaneacionPorVotante.getOrDefault(idVotante, 0));
                dto.setFaseSeguimiento(faseSeguimientoOk ? 1 : 0);
                dto.setFaseEvaluacion(verificarFaseEvaluacion(indicadores));

                resultado.add(dto);
            } catch (Exception e) {
                log.error("Error procesando votante: {}", e.getMessage());
            }
        }
        return resultado;
    }

    private List<DashboardAvanceDto> agruparPorUnidad(List<DashboardAvanceDto> personas) {
        Map<String, List<DashboardAvanceDto>> porUnidad = personas.stream()
                .filter(p -> p.getCodUnidad() != null && !p.getCodUnidad().isEmpty())
                .collect(Collectors.groupingBy(DashboardAvanceDto::getCodUnidad));

        List<DashboardAvanceDto> resultado = new ArrayList<>();
        for (Map.Entry<String, List<DashboardAvanceDto>> entry : porUnidad.entrySet()) {
            String codUnidad = entry.getKey();
            List<DashboardAvanceDto> lista = entry.getValue();

            DashboardAvanceDto dto = new DashboardAvanceDto();
            dto.setCodigo(codUnidad);
            UnidadOrganizativa unidad = unidadOrganizativaRepository.findFirstByCodUnidad(codUnidad);
            dto.setNombre(unidad != null ? unidad.getDescripcion() : codUnidad);
            dto.setCodUnidad(codUnidad);
            dto.setTipo("UNIDAD");
            sumarMetricas(dto, lista);
            dto.setDesglose(lista);
            resultado.add(dto);
        }
        return resultado;
    }

    private List<DashboardAvanceDto> agruparPorOrgano(List<DashboardAvanceDto> personas) {
        List<UnidadOrganizativa> todasUnidades = unidadOrganizativaRepository.findAll();
        Map<String, String> unidadAPadre = new HashMap<>();
        for (UnidadOrganizativa u : todasUnidades) {
            unidadAPadre.put(u.getCodUnidad(),
                    (u.getCodPadre() != null && !u.getCodPadre().isEmpty()) ? u.getCodPadre() : u.getCodUnidad());
        }

        Map<String, List<DashboardAvanceDto>> porOrgano = personas.stream()
                .filter(p -> p.getCodUnidad() != null && !p.getCodUnidad().isEmpty())
                .collect(Collectors.groupingBy(p ->
                        unidadAPadre.getOrDefault(p.getCodUnidad(), p.getCodUnidad())));

        List<DashboardAvanceDto> resultado = new ArrayList<>();
        for (Map.Entry<String, List<DashboardAvanceDto>> entry : porOrgano.entrySet()) {
            String codOrgano = entry.getKey();
            List<DashboardAvanceDto> lista = entry.getValue();

            // Agrupar personas por unidad dentro del órgano
            List<DashboardAvanceDto> unidades = agruparPorUnidad(lista);

            DashboardAvanceDto dto = new DashboardAvanceDto();
            dto.setCodigo(codOrgano);
            UnidadOrganizativa organo = unidadOrganizativaRepository.findFirstByCodUnidad(codOrgano);
            dto.setNombre(organo != null ? organo.getDescripcion() : codOrgano);
            dto.setTipo("ORGANO");
            sumarMetricas(dto, lista); // suma sobre personas, no sobre unidades intermedias
            dto.setDesglose(unidades);
            resultado.add(dto);
        }
        return resultado;
    }

    private void sumarMetricas(DashboardAvanceDto dest, List<DashboardAvanceDto> fuente) {
        dest.setTotalTrabajadores(fuente.stream().mapToInt(DashboardAvanceDto::getTotalTrabajadores).sum());
        dest.setTotalIndicadores(fuente.stream().mapToInt(DashboardAvanceDto::getTotalIndicadores).sum());
        dest.setIndicadoresCompletados(fuente.stream().mapToInt(DashboardAvanceDto::getIndicadoresCompletados).sum());
        dest.setEvidenciasIniciales(fuente.stream().mapToInt(DashboardAvanceDto::getEvidenciasIniciales).sum());
        dest.setEvidenciasSeguimiento(0);
        dest.setEvidenciasFinales(fuente.stream().mapToInt(DashboardAvanceDto::getEvidenciasFinales).sum());
        int total = fuente.stream().mapToInt(DashboardAvanceDto::getTotalEvidencias).sum();
        int subidas = fuente.stream().mapToInt(DashboardAvanceDto::getEvidenciasSubidas).sum();
        dest.setTotalEvidencias(total);
        dest.setEvidenciasSubidas(subidas);
        dest.setPorcentajeAvance(total > 0 ? roundDouble((subidas * 100.0) / total) : 0.0);
        dest.setConCalificacion(fuente.stream().mapToInt(DashboardAvanceDto::getConCalificacion).sum());
        dest.setSinCalificacion(fuente.stream().mapToInt(DashboardAvanceDto::getSinCalificacion).sum());
        dest.setDistinguidos(fuente.stream().mapToInt(DashboardAvanceDto::getDistinguidos).sum());
        dest.setFasePlaneacion(fuente.stream().mapToInt(DashboardAvanceDto::getFasePlaneacion).sum());
        dest.setFaseSeguimiento(fuente.stream().mapToInt(DashboardAvanceDto::getFaseSeguimiento).sum());
        dest.setFaseEvaluacion(fuente.stream().mapToInt(DashboardAvanceDto::getFaseEvaluacion).sum());
    }

    private List<Indicador> obtenerIndicadoresPorVotante(int idVotante, int anio) {
        try {
            String sql = "SELECT * FROM indicador WHERE id_votante = :idVotante AND anio = :anio AND estado = true";
            Query query = entityManager.createNativeQuery(sql, Indicador.class);
            query.setParameter("idVotante", idVotante);
            query.setParameter("anio", anio);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error al obtener indicadores por votante {}: {}", idVotante, e.getMessage());
            return new ArrayList<>();
        }
    }

    private Double roundDouble(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    /**
     * Fase 3: Evaluación — todos los indicadores con valor alcanzado > 0
     */
    private int verificarFaseEvaluacion(List<Indicador> indicadores) {
        try {
            if (indicadores == null || indicadores.isEmpty()) return 0;

            List<Long> idsPrioridad = indicadores.stream()
                    .filter(ind -> ind.getPrioridad() != null)
                    .map(ind -> Long.valueOf(ind.getPrioridad().getIdPrioridad()))
                    .distinct()
                    .collect(Collectors.toList());

            if (idsPrioridad.isEmpty()) return 0;

            List<ValorAlcanzadoPrioridad> valores = valorAlcanzadoPrioridadRepository.findByIdPrioridadIn(idsPrioridad);
            Set<Long> prioridadesConValor = valores.stream()
                    .filter(v -> v.getValorAlcanzado() != null && v.getValorAlcanzado().compareTo(BigDecimal.ZERO) > 0)
                    .map(ValorAlcanzadoPrioridad::getIdPrioridad)
                    .collect(Collectors.toSet());

            for (Long id : idsPrioridad) {
                if (!prioridadesConValor.contains(id)) return 0;
            }
            return 1;
        } catch (Exception e) {
            log.warn("Error verificando fase evaluación: {}", e.getMessage());
            return 0;
        }
    }
}
