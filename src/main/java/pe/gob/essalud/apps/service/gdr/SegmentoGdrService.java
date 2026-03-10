package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.SegmentoGdr;
import pe.gob.essalud.apps.repository.gdr.SegmentoGdrRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentoGdrService {

    private final SegmentoGdrRepository segmentoGdrRepository;
    private final MaestroGdrService maestroGdrService;

    // Segmentos válidos
    public static final Set<String> SEGMENTOS_VALIDOS = new HashSet<>(Arrays.asList(
        "Directivo",
        "Ejecutor",
        "Operador y asistencia",
        "Mando medio",
        "Funcionario"
    ));

    /**
     * Verificar si el usuario actual es Maestro GDR
     */
    public boolean tieneAcceso() {
        return maestroGdrService.esMaestroGdr();
    }

    /**
     * Listar todos los segmentos
     */
    public List<SegmentoGdr> listarTodos() {
        return segmentoGdrRepository.findAllByOrderByFechaCreacionDesc();
    }

    /**
     * Buscar por DNI (parcial)
     */
    public List<SegmentoGdr> buscarPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return listarTodos();
        }
        return segmentoGdrRepository.findByNumeroDocumentoContaining(dni.trim());
    }

    /**
     * Obtener segmento por DNI exacto
     */
    public Optional<SegmentoGdr> obtenerPorDni(String dni) {
        return segmentoGdrRepository.findByNumeroDocumento(dni);
    }

    /**
     * Agregar nuevo segmento
     */
    @Transactional("gdrTransactionManager")
    public SegmentoGdr agregar(String dni, String segmento) {
        log.info("Agregando segmento para DNI: {}, Segmento: {}", dni, segmento);
        
        // Validar segmento
        if (!SEGMENTOS_VALIDOS.contains(segmento)) {
            throw new IllegalArgumentException("Segmento inválido: " + segmento + 
                ". Valores permitidos: " + String.join(", ", SEGMENTOS_VALIDOS));
        }
        
        // Verificar si ya existe
        Optional<SegmentoGdr> existente = segmentoGdrRepository.findByNumeroDocumento(dni);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un registro para el DNI: " + dni);
        }
        
        SegmentoGdr nuevo = new SegmentoGdr(dni, segmento);
        SegmentoGdr guardado = segmentoGdrRepository.save(nuevo);
        log.info("Segmento creado con ID: {}", guardado.getId());
        return guardado;
    }

    /**
     * Actualizar segmento existente
     */
    @Transactional("gdrTransactionManager")
    public boolean actualizar(String dni, String segmento) {
        log.info("Actualizando segmento para DNI: {}, Segmento: {}", dni, segmento);
        
        // Validar segmento
        if (!SEGMENTOS_VALIDOS.contains(segmento)) {
            throw new IllegalArgumentException("Segmento inválido: " + segmento + 
                ". Valores permitidos: " + String.join(", ", SEGMENTOS_VALIDOS));
        }
        
        int result = segmentoGdrRepository.actualizarSegmentoPorDni(dni, segmento);
        log.info("Resultado actualización: {}", result > 0);
        return result > 0;
    }

    /**
     * Eliminar segmento por DNI
     */
    @Transactional("gdrTransactionManager")
    public boolean eliminar(String dni) {
        log.info("Eliminando segmento para DNI: {}", dni);
        int result = segmentoGdrRepository.eliminarPorDni(dni);
        log.info("Resultado eliminación: {}", result > 0);
        return result > 0;
    }

    /**
     * Carga masiva desde lista de registros
     * @param registros Lista de Map con "dni" y "segmento"
     * @return Map con resultado: insertados, actualizados, errores
     */
    @Transactional("gdrTransactionManager")
    public Map<String, Object> cargaMasiva(List<Map<String, String>> registros) {
        log.info("Iniciando carga masiva de {} registros", registros.size());
        
        Map<String, Object> resultado = new HashMap<>();
        List<String> errores = new ArrayList<>();
        int insertados = 0;
        int actualizados = 0;

        // Primero validar TODOS los registros
        for (int i = 0; i < registros.size(); i++) {
            Map<String, String> registro = registros.get(i);
            String dni = registro.get("dni");
            String segmento = registro.get("segmento");
            int fila = i + 1;

            if (dni == null || dni.trim().isEmpty()) {
                errores.add("Fila " + fila + ": DNI es requerido");
                continue;
            }

            if (segmento == null || segmento.trim().isEmpty()) {
                errores.add("Fila " + fila + ": Segmento es requerido");
                continue;
            }

            if (!SEGMENTOS_VALIDOS.contains(segmento.trim())) {
                errores.add("Fila " + fila + ": Segmento inválido '" + segmento + 
                    "'. Valores permitidos: " + String.join(", ", SEGMENTOS_VALIDOS));
            }
        }

        // Si hay errores de validación, rechazar todo el Excel
        if (!errores.isEmpty()) {
            resultado.put("success", false);
            resultado.put("insertados", 0);
            resultado.put("actualizados", 0);
            resultado.put("errores", errores);
            resultado.put("mensaje", "Excel rechazado por errores de validación. Corrija los errores y vuelva a intentar.");
            return resultado;
        }

        // Procesar registros (todos válidos)
        for (Map<String, String> registro : registros) {
            String dni = registro.get("dni").trim();
            String segmento = registro.get("segmento").trim();

            try {
                Optional<SegmentoGdr> existente = segmentoGdrRepository.findByNumeroDocumento(dni);
                
                if (existente.isPresent()) {
                    // Actualizar existente
                    segmentoGdrRepository.actualizarSegmentoPorDni(dni, segmento);
                    actualizados++;
                } else {
                    // Insertar nuevo
                    SegmentoGdr nuevo = new SegmentoGdr(dni, segmento);
                    segmentoGdrRepository.save(nuevo);
                    insertados++;
                }
            } catch (Exception e) {
                log.error("Error procesando DNI {}: {}", dni, e.getMessage());
                errores.add("Error procesando DNI " + dni + ": " + e.getMessage());
            }
        }

        resultado.put("success", errores.isEmpty());
        resultado.put("insertados", insertados);
        resultado.put("actualizados", actualizados);
        resultado.put("errores", errores);
        resultado.put("mensaje", "Carga completada: " + insertados + " insertados, " + actualizados + " actualizados");
        
        log.info("Carga masiva completada: {} insertados, {} actualizados, {} errores", 
            insertados, actualizados, errores.size());
        
        return resultado;
    }

    /**
     * Obtener lista de segmentos válidos
     */
    public Set<String> getSegmentosValidos() {
        return SEGMENTOS_VALIDOS;
    }

    /**
     * Contar total de registros
     */
    public long contar() {
        return segmentoGdrRepository.count();
    }
}
