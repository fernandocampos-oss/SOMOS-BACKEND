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
        log.info("Generando dashboard de avance - tipo: {}, año: {}, redes: {}", 
                 request.getTipoAgrupacion(), request.getAnio(), request.getListCodRed());

        // Validar y establecer defaults
        if (request.getAnio() == null || request.getAnio().isEmpty()) {
            request.setAnio(String.valueOf(java.time.Year.now().getValue()));
        }
        if (request.getTipoAgrupacion() == null || request.getTipoAgrupacion().isEmpty()) {
            request.setTipoAgrupacion("PERSONA");
        }

        // Obtener todos los votantes con indicadores según filtros
        List<Object[]> datos = obtenerDatosBase(request);
        
        if (datos == null || datos.isEmpty()) {
            log.info("No se encontraron datos para los filtros especificados");
            return new ArrayList<>();
        }
        
        String tipo = request.getTipoAgrupacion().toUpperCase();
        switch (tipo) {
            case "PERSONA":
                return agruparPorPersona(datos, Integer.parseInt(request.getAnio()));
            case "UNIDAD":
                return agruparPorUnidad(datos, Integer.parseInt(request.getAnio()));
            case "ORGANO":
                return agruparPorOrgano(datos, Integer.parseInt(request.getAnio()));
            default:
                return agruparPorPersona(datos, Integer.parseInt(request.getAnio()));
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

    private List<DashboardAvanceDto> agruparPorPersona(List<Object[]> datos, int anio) {
        List<DashboardAvanceDto> resultado = new ArrayList<>();

        for (Object[] row : datos) {
            try {
                Integer idVotante = ((Number) row[0]).intValue();
                String numeroDocumento = row[1] != null ? (String) row[1] : "";
                String nombres = row[2] != null ? (String) row[2] : "";
                String apellidos = row[3] != null ? (String) row[3] : "";
                String codUnidad = row[5] != null ? (String) row[5] : "";

                DashboardAvanceDto dto = new DashboardAvanceDto();
                dto.setCodigo(numeroDocumento);
                dto.setNombre((apellidos + " " + nombres).trim());
                dto.setTipo("PERSONA");
                dto.setTotalTrabajadores(1);

                // Obtener indicadores del votante
                List<Indicador> indicadores = obtenerIndicadoresPorVotante(idVotante, anio);
                int numIndicadores = indicadores != null ? indicadores.size() : 0;
                dto.setTotalIndicadores(numIndicadores);
                log.info("Votante {} ({}): {} indicadores encontrados", numeroDocumento, idVotante, numIndicadores);
                
                // Contadores de evidencias
                int totalEvidenciasCiclo = 0;      // Evidencias que no son SUSTENTO FINAL
                int evidenciasCicloSubidas = 0;   // Evidencias de ciclo con archivo subido
                int totalEvidenciasFinales = 0;   // Evidencias SUSTENTO FINAL
                int evidenciasFinalesSubidas = 0; // Evidencias finales con archivo subido
                int totalEvidencias = 0;
                int evidenciasSubidas = 0;
                int indicadoresCompletados = 0;

                if (indicadores != null) {
                    for (Indicador ind : indicadores) {
                        List<Evidencia> evidencias = evidenciaRepository.listEvidenciaByIdIndicador(ind.getIdIndicador());
                        if (evidencias == null) evidencias = new ArrayList<>();
                        
                        log.info("  Indicador {}: {} evidencias", ind.getIdIndicador(), evidencias.size());
                        
                        int evidenciasIndicadorSubidas = 0;
                        int totalEvidenciasIndicador = evidencias.size();
                        
                        for (Evidencia ev : evidencias) {
                            totalEvidencias++;
                            String rutaFile = ev.getSustentoRutaFile();
                            boolean tieneArchivo = rutaFile != null && !rutaFile.trim().isEmpty();
                            
                            log.info("    Evidencia {}: desc='{}', sustentoRutaFile='{}', tieneArchivo={}", 
                                    ev.getIdEvidencia(), ev.getDescripcion(), rutaFile, tieneArchivo);
                            
                            if (tieneArchivo) {
                                evidenciasSubidas++;
                                evidenciasIndicadorSubidas++;
                            }
                            
                            // Clasificar: SUSTENTO FINAL = evidencia final, resto = evidencias de ciclo
                            String desc = ev.getDescripcion() != null ? ev.getDescripcion().toUpperCase().trim() : "";
                            if ("SUSTENTO FINAL".equals(desc)) {
                                totalEvidenciasFinales++;
                                if (tieneArchivo) {
                                    evidenciasFinalesSubidas++;
                                }
                            } else {
                                totalEvidenciasCiclo++;
                                if (tieneArchivo) {
                                    evidenciasCicloSubidas++;
                                }
                            }
                        }
                        
                        // Indicador completado si todas sus evidencias tienen archivo
                        if (totalEvidenciasIndicador > 0 && evidenciasIndicadorSubidas == totalEvidenciasIndicador) {
                            indicadoresCompletados++;
                        }
                    }
                }
                
                log.info("Votante {}: totalEvid={}, subidas={}, cicloParcial={}/{}, finalesParcial={}/{}", 
                         numeroDocumento, totalEvidencias, evidenciasSubidas,
                         evidenciasCicloSubidas, totalEvidenciasCiclo,
                         evidenciasFinalesSubidas, totalEvidenciasFinales);

                dto.setIndicadoresCompletados(indicadoresCompletados);
                // Para la UI: 
                // Evid. Iniciales = TOTAL de evidencias que no son SUSTENTO FINAL
                // Evid. Seguimiento = 0 (no aplica en el modelo actual)  
                // Evid. Finales = TOTAL de evidencias SUSTENTO FINAL
                // Evid. Subidas = total de evidencias con archivo cargado
                dto.setEvidenciasIniciales(totalEvidenciasCiclo);
                dto.setEvidenciasSeguimiento(0);
                dto.setEvidenciasFinales(totalEvidenciasFinales);
                dto.setTotalEvidencias(totalEvidencias);
                dto.setEvidenciasSubidas(evidenciasSubidas);
                
                // Calcular porcentaje de avance
                double porcentaje = totalEvidencias > 0 
                        ? (evidenciasSubidas * 100.0) / totalEvidencias 
                        : 0.0;
                dto.setPorcentajeAvance(roundDouble(porcentaje));

                // Verificar resultado final
                try {
                    Optional<ResultadosFinales> resultadoFinal = resultadosFinalesRepository
                            .findByIdVotanteAndAnio(Long.valueOf(idVotante), anio);
                    if (resultadoFinal.isPresent()) {
                        dto.setConCalificacion(1);
                        dto.setSinCalificacion(0);
                        if ("SI".equalsIgnoreCase(resultadoFinal.get().getRendimientoDistinguido())) {
                            dto.setDistinguidos(1);
                        } else {
                            dto.setDistinguidos(0);
                        }
                    } else {
                        dto.setConCalificacion(0);
                        dto.setSinCalificacion(1);
                        dto.setDistinguidos(0);
                    }
                } catch (Exception ex) {
                    log.warn("Error al obtener resultado final para votante {}: {}", idVotante, ex.getMessage());
                    dto.setConCalificacion(0);
                    dto.setSinCalificacion(1);
                    dto.setDistinguidos(0);
                }

                // === CALCULAR FASES GDR ===
                // Fase 1: Planeación (formato registrado)
                int fasePlaneacion = verificarFasePlaneacion(idVotante, anio);
                dto.setFasePlaneacion(fasePlaneacion);
                
                // Fase 2: Seguimiento (todas las evidencias con comentario no vacío)
                int faseSeguimiento = verificarFaseSeguimiento(indicadores);
                dto.setFaseSeguimiento(faseSeguimiento);
                
                // Fase 3: Evaluación (todos los indicadores con valor alcanzado > 0)
                int faseEvaluacion = verificarFaseEvaluacion(indicadores);
                dto.setFaseEvaluacion(faseEvaluacion);
                
                log.info("Votante {} - Fases: Planeacion={}, Seguimiento={}, Evaluacion={}", 
                         numeroDocumento, fasePlaneacion, faseSeguimiento, faseEvaluacion);

                resultado.add(dto);
            } catch (Exception e) {
                log.error("Error procesando votante: {}", e.getMessage());
            }
        }

        return resultado;
    }

    private List<DashboardAvanceDto> agruparPorUnidad(List<Object[]> datos, int anio) {
        // Agrupar por código de unidad
        Map<String, List<Object[]>> porUnidad = datos.stream()
                .filter(row -> row[5] != null)
                .collect(Collectors.groupingBy(row -> (String) row[5]));

        List<DashboardAvanceDto> resultado = new ArrayList<>();

        for (Map.Entry<String, List<Object[]>> entry : porUnidad.entrySet()) {
            String codUnidad = entry.getKey();
            List<Object[]> votantesUnidad = entry.getValue();

            // Obtener datos agregados por persona para esta unidad
            List<DashboardAvanceDto> personasUnidad = agruparPorPersona(votantesUnidad, anio);
            
            DashboardAvanceDto dto = new DashboardAvanceDto();
            dto.setCodigo(codUnidad);
            
            // Obtener nombre de la unidad
            UnidadOrganizativa unidad = unidadOrganizativaRepository.findFirstByCodUnidad(codUnidad);
            dto.setNombre(unidad != null ? unidad.getDescripcion() : codUnidad);
            dto.setTipo("UNIDAD");

            // Sumar métricas
            dto.setTotalTrabajadores(personasUnidad.size());
            dto.setTotalIndicadores(personasUnidad.stream().mapToInt(DashboardAvanceDto::getTotalIndicadores).sum());
            dto.setIndicadoresCompletados(personasUnidad.stream().mapToInt(DashboardAvanceDto::getIndicadoresCompletados).sum());
            dto.setEvidenciasIniciales(personasUnidad.stream().mapToInt(DashboardAvanceDto::getEvidenciasIniciales).sum());
            dto.setEvidenciasSeguimiento(personasUnidad.stream().mapToInt(DashboardAvanceDto::getEvidenciasSeguimiento).sum());
            dto.setEvidenciasFinales(personasUnidad.stream().mapToInt(DashboardAvanceDto::getEvidenciasFinales).sum());
            dto.setTotalEvidencias(personasUnidad.stream().mapToInt(DashboardAvanceDto::getTotalEvidencias).sum());
            dto.setEvidenciasSubidas(personasUnidad.stream().mapToInt(DashboardAvanceDto::getEvidenciasSubidas).sum());
            dto.setConCalificacion(personasUnidad.stream().mapToInt(DashboardAvanceDto::getConCalificacion).sum());
            dto.setSinCalificacion(personasUnidad.stream().mapToInt(DashboardAvanceDto::getSinCalificacion).sum());
            dto.setDistinguidos(personasUnidad.stream().mapToInt(DashboardAvanceDto::getDistinguidos).sum());
            
            // Sumar conteos de fases
            dto.setFasePlaneacion(personasUnidad.stream().mapToInt(DashboardAvanceDto::getFasePlaneacion).sum());
            dto.setFaseSeguimiento(personasUnidad.stream().mapToInt(DashboardAvanceDto::getFaseSeguimiento).sum());
            dto.setFaseEvaluacion(personasUnidad.stream().mapToInt(DashboardAvanceDto::getFaseEvaluacion).sum());

            // Calcular porcentaje promedio
            int totalEv = dto.getTotalEvidencias();
            int subidas = dto.getEvidenciasSubidas();
            double porcentaje = totalEv > 0 ? (subidas * 100.0) / totalEv : 0.0;
            dto.setPorcentajeAvance(roundDouble(porcentaje));

            dto.setDesglose(personasUnidad);

            resultado.add(dto);
        }

        return resultado;
    }

    private List<DashboardAvanceDto> agruparPorOrgano(List<Object[]> datos, int anio) {
        // Primero, identificar los órganos (unidades padre)
        Map<String, String> unidadAPadre = new HashMap<>();
        List<UnidadOrganizativa> todasUnidades = unidadOrganizativaRepository.findAll();
        
        for (UnidadOrganizativa u : todasUnidades) {
            if (u.getCodPadre() != null && !u.getCodPadre().isEmpty()) {
                unidadAPadre.put(u.getCodUnidad(), u.getCodPadre());
            } else {
                // Es un órgano (nivel superior)
                unidadAPadre.put(u.getCodUnidad(), u.getCodUnidad());
            }
        }

        // Agrupar datos por órgano
        Map<String, List<Object[]>> porOrgano = datos.stream()
                .filter(row -> row[5] != null)
                .collect(Collectors.groupingBy(row -> {
                    String codUnidad = (String) row[5];
                    return unidadAPadre.getOrDefault(codUnidad, codUnidad);
                }));

        List<DashboardAvanceDto> resultado = new ArrayList<>();

        for (Map.Entry<String, List<Object[]>> entry : porOrgano.entrySet()) {
            String codOrgano = entry.getKey();
            List<Object[]> votantesOrgano = entry.getValue();

            // Obtener datos agregados por unidad para este órgano
            List<DashboardAvanceDto> unidadesOrgano = agruparPorUnidad(votantesOrgano, anio);

            DashboardAvanceDto dto = new DashboardAvanceDto();
            dto.setCodigo(codOrgano);

            // Obtener nombre del órgano
            UnidadOrganizativa organo = unidadOrganizativaRepository.findFirstByCodUnidad(codOrgano);
            dto.setNombre(organo != null ? organo.getDescripcion() : codOrgano);
            dto.setTipo("ORGANO");

            // Sumar métricas de todas las unidades
            dto.setTotalTrabajadores(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getTotalTrabajadores).sum());
            dto.setTotalIndicadores(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getTotalIndicadores).sum());
            dto.setIndicadoresCompletados(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getIndicadoresCompletados).sum());
            dto.setEvidenciasIniciales(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getEvidenciasIniciales).sum());
            dto.setEvidenciasSeguimiento(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getEvidenciasSeguimiento).sum());
            dto.setEvidenciasFinales(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getEvidenciasFinales).sum());
            dto.setTotalEvidencias(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getTotalEvidencias).sum());
            dto.setEvidenciasSubidas(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getEvidenciasSubidas).sum());
            dto.setConCalificacion(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getConCalificacion).sum());
            dto.setSinCalificacion(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getSinCalificacion).sum());
            dto.setDistinguidos(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getDistinguidos).sum());
            
            // Sumar conteos de fases
            dto.setFasePlaneacion(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getFasePlaneacion).sum());
            dto.setFaseSeguimiento(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getFaseSeguimiento).sum());
            dto.setFaseEvaluacion(unidadesOrgano.stream().mapToInt(DashboardAvanceDto::getFaseEvaluacion).sum());

            // Calcular porcentaje
            int totalEv = dto.getTotalEvidencias();
            int subidas = dto.getEvidenciasSubidas();
            double porcentaje = totalEv > 0 ? (subidas * 100.0) / totalEv : 0.0;
            dto.setPorcentajeAvance(roundDouble(porcentaje));

            dto.setDesglose(unidadesOrgano);

            resultado.add(dto);
        }

        return resultado;
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
     * Fase 1: Planeación - Verificar si el formato está registrado
     * (reunion_establecimiento_metas.confirmado = true)
     */
    private int verificarFasePlaneacion(int idVotante, int anio) {
        try {
            String periodo = String.valueOf(anio);
            String sql = "SELECT confirmado FROM reunion_establecimiento_metas " +
                         "WHERE id_votante_evaluado = :idVotante AND periodo = :periodo";
            Query query = gdrEntityManager.createNativeQuery(sql);
            query.setParameter("idVotante", (long) idVotante);
            query.setParameter("periodo", periodo);
            
            List<?> results = query.getResultList();
            if (results != null && !results.isEmpty()) {
                Object result = results.get(0);
                if (result instanceof Boolean) {
                    return Boolean.TRUE.equals(result) ? 1 : 0;
                } else if (result instanceof Number) {
                    return ((Number) result).intValue() == 1 ? 1 : 0;
                }
            }
            return 0;
        } catch (Exception e) {
            log.warn("Error verificando fase planeación para votante {}: {}", idVotante, e.getMessage());
            return 0;
        }
    }
    
    /**
     * Fase 2: Seguimiento - Verificar que TODAS las evidencias tengan comentario no vacío
     * (comentario debe ser: logrado, proceso, no_presento, si_presenta, no_presenta)
     */
    private int verificarFaseSeguimiento(List<Indicador> indicadores) {
        try {
            if (indicadores == null || indicadores.isEmpty()) {
                return 0;
            }
            
            for (Indicador ind : indicadores) {
                List<Evidencia> evidencias = evidenciaRepository.listEvidenciaByIdIndicador(ind.getIdIndicador());
                if (evidencias == null || evidencias.isEmpty()) {
                    return 0; // Sin evidencias = no completó seguimiento
                }
                
                for (Evidencia ev : evidencias) {
                    String comentario = ev.getComentario();
                    // Si alguna evidencia NO tiene comentario válido, fase no completada
                    if (comentario == null || comentario.trim().isEmpty()) {
                        return 0;
                    }
                }
            }
            // Todas las evidencias tienen comentario
            return 1;
        } catch (Exception e) {
            log.warn("Error verificando fase seguimiento: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Fase 3: Evaluación - Verificar que TODOS los indicadores tengan valor alcanzado > 0
     * (buscar en tabla valor_alcanzado_prioridad por id_prioridad)
     */
    private int verificarFaseEvaluacion(List<Indicador> indicadores) {
        try {
            if (indicadores == null || indicadores.isEmpty()) {
                return 0;
            }
            
            // Obtener todos los ids de prioridad de los indicadores
            List<Long> idsPrioridad = indicadores.stream()
                    .filter(ind -> ind.getPrioridad() != null)
                    .map(ind -> Long.valueOf(ind.getPrioridad().getIdPrioridad()))
                    .distinct()
                    .collect(Collectors.toList());
            
            if (idsPrioridad.isEmpty()) {
                return 0;
            }
            
            // Buscar valores alcanzados
            List<ValorAlcanzadoPrioridad> valores = valorAlcanzadoPrioridadRepository.findByIdPrioridadIn(idsPrioridad);
            
            // Verificar que TODOS los indicadores tengan valor alcanzado > 0
            Set<Long> prioridadesConValor = valores.stream()
                    .filter(v -> v.getValorAlcanzado() != null && v.getValorAlcanzado().compareTo(BigDecimal.ZERO) > 0)
                    .map(ValorAlcanzadoPrioridad::getIdPrioridad)
                    .collect(Collectors.toSet());
            
            // Todos los IDs de prioridad deben tener valor
            for (Long idPrioridad : idsPrioridad) {
                if (!prioridadesConValor.contains(idPrioridad)) {
                    return 0;
                }
            }
            
            return 1;
        } catch (Exception e) {
            log.warn("Error verificando fase evaluación: {}", e.getMessage());
            return 0;
        }
    }
}
