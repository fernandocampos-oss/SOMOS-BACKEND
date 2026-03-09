package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.CambiarSegmentoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.CargaMasivaEvaluadorResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.CargaMasivaEvaluadorResponseDto.ErrorCargaDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.CargaMasivaEvaluadorResponseDto.EvaluadorPreviewDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvaluadorListResponseDto;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.VotanteRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EquipoRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestionEvaluadoresService {

    private static final int SEGMENTO_EVALUADOR = 3;
    
    private final VotanteRepository votanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;

    /**
     * Lista evaluadores paginado (solo DNI y nombre - sin consultar tabla usuario)
     */
    public Map<String, Object> listarEvaluadoresPaginado(int page, int size, String filtro, Boolean soloConTrabajadores) {
        log.info("Listando evaluadores - página: {}, tamaño: {}, filtro: {}, soloConTrabajadores: {}", page, size, filtro, soloConTrabajadores);
        long inicio = System.currentTimeMillis();
        
        // Obtener lista de jefes que tienen trabajadores
        List<Integer> jefesConTrabajadores = equipoRepository.findJefesConTrabajadores();
        Set<Integer> setJefesConTrabajadores = new HashSet<>(jefesConTrabajadores);
        log.info("Jefes con trabajadores: {}", jefesConTrabajadores.size());
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("numeroDocumento").ascending());
        Page<Votante> pageResult;
        
        boolean hayFiltro = filtro != null && !filtro.trim().isEmpty();
        boolean quiereSoloConTrabajadores = Boolean.TRUE.equals(soloConTrabajadores);
        
        // Si quiere solo con trabajadores pero no hay ninguno, devolver vacío
        if (quiereSoloConTrabajadores && jefesConTrabajadores.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("content", new ArrayList<>());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("size", size);
            response.put("totalConTrabajadores", 0);
            log.info("No hay evaluadores con trabajadores asignados");
            return response;
        }
        
        // Seleccionar query según filtros
        if (quiereSoloConTrabajadores && hayFiltro) {
            // Filtrar por trabajadores Y por DNI
            pageResult = votanteRepository.findByIdSegmentoAndIdVotanteInAndNumeroDocumentoContaining(
                SEGMENTO_EVALUADOR, jefesConTrabajadores, filtro.trim(), pageable);
        } else if (quiereSoloConTrabajadores) {
            // Solo filtrar por trabajadores
            pageResult = votanteRepository.findByIdSegmentoAndIdVotanteIn(
                SEGMENTO_EVALUADOR, jefesConTrabajadores, pageable);
        } else if (hayFiltro) {
            // Solo filtrar por DNI
            pageResult = votanteRepository.findByIdSegmentoAndNumeroDocumentoContaining(
                SEGMENTO_EVALUADOR, filtro.trim(), pageable);
        } else {
            // Sin filtros
            pageResult = votanteRepository.findByIdSegmento(SEGMENTO_EVALUADOR, pageable);
        }
        
        List<EvaluadorListResponseDto> content = new ArrayList<>();
        for (Votante votante : pageResult.getContent()) {
            boolean tieneTrabajadores = setJefesConTrabajadores.contains(votante.getIdVotante());
            
            EvaluadorListResponseDto dto = new EvaluadorListResponseDto();
            dto.setIdVotante(votante.getIdVotante());
            dto.setNumeroDocumento(votante.getNumeroDocumento());
            dto.setNombreCompleto((votante.getNombres() + " " + votante.getApellidos()).trim());
            dto.setIdSegmento(votante.getIdSegmento());
            dto.setTieneTrabajadores(tieneTrabajadores);
            content.add(dto);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        response.put("totalConTrabajadores", jefesConTrabajadores.size());
        
        log.info("Listado completado en {} ms - Total: {}", System.currentTimeMillis() - inicio, pageResult.getTotalElements());
        return response;
    }

    /**
     * Lista todos los evaluadores (solo DNI y nombre - para export)
     */
    public List<EvaluadorListResponseDto> listarEvaluadores() {
        log.info("Iniciando listado de evaluadores para export...");
        long inicio = System.currentTimeMillis();
        
        List<Votante> evaluadores = votanteRepository.findByIdSegmento(SEGMENTO_EVALUADOR);
        
        List<EvaluadorListResponseDto> resultado = new ArrayList<>();
        for (Votante votante : evaluadores) {
            EvaluadorListResponseDto dto = new EvaluadorListResponseDto();
            dto.setIdVotante(votante.getIdVotante());
            dto.setNumeroDocumento(votante.getNumeroDocumento());
            dto.setNombreCompleto((votante.getNombres() + " " + votante.getApellidos()).trim());
            dto.setIdSegmento(votante.getIdSegmento());
            resultado.add(dto);
        }
        
        log.info("Listado completado en {} ms - Total: {}", System.currentTimeMillis() - inicio, resultado.size());
        return resultado;
    }

    /**
     * Buscar trabajador por DNI (para agregar como evaluador)
     */
    public EvaluadorListResponseDto buscarPorDni(String dni) {
        // Primero buscar en usuario
        Usuario usuario = usuarioRepository.findDocumento(dni);
        
        if (usuario == null) {
            return null;
        }
        
        EvaluadorListResponseDto dto = new EvaluadorListResponseDto();
        dto.setNumeroDocumento(usuario.getNumeroDocumento());
        dto.setNombreCompleto((usuario.getNombres() + " " + usuario.getApellidos()).trim());
        dto.setCargo(usuario.getCargo());
        dto.setUnidad(usuario.getCodigoUnidad());
        dto.setRegimen(usuario.getRegimen());
        
        // Verificar si ya existe en votante
        Optional<Votante> votanteOpt = votanteRepository.findByNumeroDocumento(dni);
        if (votanteOpt.isPresent()) {
            Votante votante = votanteOpt.get();
            dto.setIdVotante(votante.getIdVotante());
            dto.setIdSegmento(votante.getIdSegmento());
        }
        
        return dto;
    }

    /**
     * Agregar un evaluador individual
     */
    @Transactional("transactionManager1")
    public EvaluadorListResponseDto agregarEvaluador(String dni) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findDocumento(dni);
        if (usuario == null) {
            throw new RuntimeException("No se encontró trabajador con DNI: " + dni);
        }
        
        // Verificar si ya existe en votante
        Optional<Votante> votanteOpt = votanteRepository.findByNumeroDocumento(dni);
        Votante votante;
        
        if (votanteOpt.isPresent()) {
            // Actualizar segmento
            votante = votanteOpt.get();
            votante.setIdSegmento(SEGMENTO_EVALUADOR);
        } else {
            // Crear nuevo votante
            int maxId = equipoRepository.getCantidadRegistro();
            votante = new Votante();
            votante.setIdVotante(maxId + 1);
            votante.setNumeroDocumento(usuario.getNumeroDocumento());
            votante.setNombres(usuario.getNombres());
            votante.setApellidos(usuario.getApellidos());
            votante.setIdSegmento(SEGMENTO_EVALUADOR);
            votante.setIdUsuario((int) usuario.getIdUsuario());
        }
        
        votanteRepository.save(votante);
        
        // Retornar datos
        EvaluadorListResponseDto dto = new EvaluadorListResponseDto();
        dto.setIdVotante(votante.getIdVotante());
        dto.setNumeroDocumento(votante.getNumeroDocumento());
        dto.setNombreCompleto((votante.getNombres() + " " + votante.getApellidos()).trim());
        dto.setCargo(usuario.getCargo());
        dto.setUnidad(usuario.getCodigoUnidad());
        dto.setRegimen(usuario.getRegimen());
        dto.setIdSegmento(votante.getIdSegmento());
        
        return dto;
    }

    /**
     * Validar carga masiva de DNIs (preview antes de confirmar)
     */
    public CargaMasivaEvaluadorResponseDto validarCargaMasiva(List<String> dnis) {
        CargaMasivaEvaluadorResponseDto response = new CargaMasivaEvaluadorResponseDto();
        List<EvaluadorPreviewDto> validos = new ArrayList<>();
        List<ErrorCargaDto> errores = new ArrayList<>();
        
        for (int i = 0; i < dnis.size(); i++) {
            int fila = i + 1;
            String dni = dnis.get(i);
            
            // Validar que no sea nulo o vacío
            if (dni == null || dni.trim().isEmpty()) {
                errores.add(new ErrorCargaDto(fila, dni, "DNI vacío"));
                continue;
            }
            
            dni = dni.trim();
            
            // Validar que solo contenga números
            if (!dni.matches("\\d+")) {
                errores.add(new ErrorCargaDto(fila, dni, "El DNI contiene caracteres no numéricos"));
                continue;
            }
            
            // Validar longitud (8 dígitos para DNI peruano)
            if (dni.length() != 8) {
                errores.add(new ErrorCargaDto(fila, dni, "El DNI debe tener 8 dígitos"));
                continue;
            }
            
            // Buscar en usuario
            Usuario usuario = usuarioRepository.findDocumento(dni);
            if (usuario == null) {
                errores.add(new ErrorCargaDto(fila, dni, "DNI no encontrado en el sistema"));
                continue;
            }
            
            // Verificar si ya es evaluador
            boolean yaEsEvaluador = false;
            Optional<Votante> votanteOpt = votanteRepository.findByNumeroDocumento(dni);
            if (votanteOpt.isPresent() && votanteOpt.get().getIdSegmento() != null 
                    && votanteOpt.get().getIdSegmento() == SEGMENTO_EVALUADOR) {
                yaEsEvaluador = true;
            }
            
            EvaluadorPreviewDto preview = new EvaluadorPreviewDto();
            preview.setFila(fila);
            preview.setDni(dni);
            preview.setNombreCompleto((usuario.getNombres() + " " + usuario.getApellidos()).trim());
            preview.setCargo(usuario.getCargo());
            preview.setUnidad(usuario.getCodigoUnidad());
            preview.setRegimen(usuario.getRegimen());
            preview.setYaEsEvaluador(yaEsEvaluador);
            
            validos.add(preview);
        }
        
        response.setExito(errores.isEmpty());
        response.setTotalProcesados(dnis.size());
        response.setTotalExitosos(validos.size());
        response.setTotalErrores(errores.size());
        response.setEvaluadoresValidos(validos);
        response.setErrores(errores);
        
        return response;
    }

    /**
     * Confirmar carga masiva (después de validación exitosa)
     */
    @Transactional("transactionManager1")
    public CargaMasivaEvaluadorResponseDto confirmarCargaMasiva(List<String> dnis) {
        CargaMasivaEvaluadorResponseDto response = new CargaMasivaEvaluadorResponseDto();
        List<EvaluadorPreviewDto> procesados = new ArrayList<>();
        List<ErrorCargaDto> errores = new ArrayList<>();
        
        for (int i = 0; i < dnis.size(); i++) {
            int fila = i + 1;
            String dni = dnis.get(i).trim();
            
            try {
                Usuario usuario = usuarioRepository.findDocumento(dni);
                if (usuario == null) {
                    errores.add(new ErrorCargaDto(fila, dni, "DNI no encontrado"));
                    continue;
                }
                
                // Verificar/crear votante
                Optional<Votante> votanteOpt = votanteRepository.findByNumeroDocumento(dni);
                Votante votante;
                
                if (votanteOpt.isPresent()) {
                    votante = votanteOpt.get();
                    votante.setIdSegmento(SEGMENTO_EVALUADOR);
                } else {
                    int maxId = equipoRepository.getCantidadRegistro();
                    votante = new Votante();
                    votante.setIdVotante(maxId + 1);
                    votante.setNumeroDocumento(usuario.getNumeroDocumento());
                    votante.setNombres(usuario.getNombres());
                    votante.setApellidos(usuario.getApellidos());
                    votante.setIdSegmento(SEGMENTO_EVALUADOR);
                    votante.setIdUsuario((int) usuario.getIdUsuario());
                }
                
                votanteRepository.save(votante);
                
                EvaluadorPreviewDto preview = new EvaluadorPreviewDto();
                preview.setFila(fila);
                preview.setDni(dni);
                preview.setNombreCompleto((usuario.getNombres() + " " + usuario.getApellidos()).trim());
                procesados.add(preview);
                
            } catch (Exception e) {
                log.error("Error procesando DNI {}: {}", dni, e.getMessage());
                errores.add(new ErrorCargaDto(fila, dni, "Error al procesar: " + e.getMessage()));
            }
        }
        
        response.setExito(errores.isEmpty());
        response.setTotalProcesados(dnis.size());
        response.setTotalExitosos(procesados.size());
        response.setTotalErrores(errores.size());
        response.setEvaluadoresValidos(procesados);
        response.setErrores(errores);
        
        return response;
    }

    /**
     * Cambiar segmento de un evaluador (quitar rol)
     */
    @Transactional("transactionManager1")
    public void cambiarSegmento(CambiarSegmentoRequestDto request) {
        Votante votante = votanteRepository.findById(request.getIdVotante())
                .orElseThrow(() -> new RuntimeException("Votante no encontrado"));
        
        votante.setIdSegmento(request.getNuevoSegmento());
        votanteRepository.save(votante);
        
        log.info("Segmento actualizado para votante {} a segmento {}", 
                request.getIdVotante(), request.getNuevoSegmento());
    }

    // ==================== ASIGNAR TRABAJADORES ====================

    /**
     * Listar trabajadores asignados a un evaluador
     */
    public List<Map<String, Object>> listarTrabajadoresPorEvaluador(Integer idEvaluador) {
        log.info("Listando trabajadores del evaluador: {}", idEvaluador);
        List<Equipo> equipos = equipoRepository.findTrabajadoresByEvaluador(idEvaluador);
        
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Equipo equipo : equipos) {
            Map<String, Object> item = new HashMap<>();
            item.put("idEquipo", equipo.getIdEquipo());
            item.put("idVotante", equipo.getIntegrante().getIdVotante());
            item.put("numeroDocumento", equipo.getIntegrante().getNumeroDocumento());
            item.put("nombreCompleto", (equipo.getIntegrante().getNombres() + " " + equipo.getIntegrante().getApellidos()).trim());
            resultado.add(item);
        }
        
        return resultado;
    }

    /**
     * Buscar trabajador por DNI para asignar
     */
    public Map<String, Object> buscarTrabajadorParaAsignar(String dni, Integer idEvaluador) {
        log.info("Buscando trabajador {} para evalaudor {}", dni, idEvaluador);
        
        Map<String, Object> resultado = new HashMap<>();
        
        // Buscar en usuario
        Usuario usuario = usuarioRepository.findDocumento(dni);
        if (usuario == null) {
            resultado.put("encontrado", false);
            resultado.put("mensaje", "DNI no encontrado en el sistema");
            return resultado;
        }
        
        resultado.put("encontrado", true);
        resultado.put("dni", usuario.getNumeroDocumento());
        resultado.put("nombreCompleto", (usuario.getNombres() + " " + usuario.getApellidos()).trim());
        
        // Verificar si existe en votante
        Optional<Votante> votanteOpt = votanteRepository.findByNumeroDocumento(dni);
        Integer idVotante = null;
        if (votanteOpt.isPresent()) {
            idVotante = votanteOpt.get().getIdVotante();
            
            // Verificar si es el mismo evaluador
            if (idVotante.equals(idEvaluador)) {
                resultado.put("puedeAsignar", false);
                resultado.put("mensaje", "Un evaluador no puede ser trabajador de sí mismo");
                return resultado;
            }
            
            // Verificar si ya tiene evaluador asignado
            Equipo equipoExistente = equipoRepository.findEvaluadorByTrabajador(idVotante);
            if (equipoExistente != null) {
                resultado.put("puedeAsignar", false);
                resultado.put("mensaje", "Este trabajador ya tiene un evaluador asignado: " + 
                        equipoExistente.getJefe().getNumeroDocumento() + " - " +
                        equipoExistente.getJefe().getNombres() + " " + equipoExistente.getJefe().getApellidos());
                resultado.put("evaluadorActual", equipoExistente.getJefe().getNumeroDocumento());
                return resultado;
            }
        }
        
        resultado.put("idVotante", idVotante);
        resultado.put("puedeAsignar", true);
        return resultado;
    }

    /**
     * Asignar trabajador a evaluador
     */
    @Transactional("transactionManager1")
    public Map<String, Object> asignarTrabajador(Integer idEvaluador, String dniTrabajador) {
        log.info("Asignando trabajador {} a evaluador {}", dniTrabajador, idEvaluador);
        
        Map<String, Object> resultado = new HashMap<>();
        
        // Verificar que el evaluador existe
        Votante evaluador = votanteRepository.findById(idEvaluador)
                .orElseThrow(() -> new RuntimeException("Evaluador no encontrado"));
        
        // Buscar usuario trabajador
        Usuario usuario = usuarioRepository.findDocumento(dniTrabajador);
        if (usuario == null) {
            resultado.put("exito", false);
            resultado.put("mensaje", "Trabajador no encontrado");
            return resultado;
        }
        
        // Obtener o crear votante para el trabajador
        Optional<Votante> votanteOpt = votanteRepository.findByNumeroDocumento(dniTrabajador);
        Votante trabajador;
        
        if (votanteOpt.isPresent()) {
            trabajador = votanteOpt.get();
            
            // Verificar que no sea el mismo
            if (trabajador.getIdVotante().equals(idEvaluador)) {
                resultado.put("exito", false);
                resultado.put("mensaje", "Un evaluador no puede ser trabajador de sí mismo");
                return resultado;
            }
            
            // Verificar si ya tiene evaluador
            Equipo equipoExistente = equipoRepository.findEvaluadorByTrabajador(trabajador.getIdVotante());
            if (equipoExistente != null) {
                resultado.put("exito", false);
                resultado.put("mensaje", "Este trabajador ya tiene un evaluador asignado");
                return resultado;
            }
        } else {
            // Crear nuevo votante
            int maxId = equipoRepository.getCantidadRegistro();
            trabajador = new Votante();
            trabajador.setIdVotante(maxId + 1);
            trabajador.setNumeroDocumento(usuario.getNumeroDocumento());
            trabajador.setNombres(usuario.getNombres());
            trabajador.setApellidos(usuario.getApellidos());
            trabajador.setIdSegmento(2); // Segmento operador/trabajador
            trabajador.setIdUsuario((int) usuario.getIdUsuario());
            votanteRepository.save(trabajador);
        }
        
        // Crear asignación en equipo
        Equipo equipo = new Equipo();
        equipo.setJefe(evaluador);
        equipo.setIntegrante(trabajador);
        equipo.setEsActivo(true);
        equipoRepository.save(equipo);
        
        resultado.put("exito", true);
        resultado.put("mensaje", "Trabajador asignado correctamente");
        resultado.put("idEquipo", equipo.getIdEquipo());
        
        log.info("Trabajador {} asignado a evaluador {} - idEquipo: {}", 
                dniTrabajador, idEvaluador, equipo.getIdEquipo());
        return resultado;
    }

    /**
     * Desasignar trabajador (quitar del equipo)
     */
    @Transactional("transactionManager1")
    public void desasignarTrabajador(Integer idEquipo) {
        log.info("Desasignando trabajador - idEquipo: {}", idEquipo);
        int result = equipoRepository.eliminarTrabajador(idEquipo);
        if (result == 0) {
            throw new RuntimeException("No se encontró la asignación");
        }
    }

    /**
     * Validar carga masiva de asignaciones (evaluador|trabajador)
     */
    public Map<String, Object> validarCargaMasivaTrabajadores(List<String[]> filas) {
        log.info("Validando carga masiva de trabajadores: {} filas", filas.size());
        
        List<Map<String, Object>> validos = new ArrayList<>();
        List<Map<String, Object>> errores = new ArrayList<>();
        
        for (int i = 0; i < filas.size(); i++) {
            int numFila = i + 1;
            String[] fila = filas.get(i);
            
            if (fila.length < 2) {
                Map<String, Object> error = new HashMap<>();
                error.put("fila", numFila);
                error.put("dniEvaluador", fila.length > 0 ? fila[0] : "");
                error.put("dniTrabajador", "");
                error.put("mensaje", "Formato inválido - se requieren 2 columnas");
                errores.add(error);
                continue;
            }
            
            String dniEvaluador = fila[0] != null ? fila[0].trim() : "";
            String dniTrabajador = fila[1] != null ? fila[1].trim() : "";
            
            Map<String, Object> item = new HashMap<>();
            item.put("fila", numFila);
            item.put("dniEvaluador", dniEvaluador);
            item.put("dniTrabajador", dniTrabajador);
            
            // Validar DNI evaluador
            if (!dniEvaluador.matches("\\d{8}")) {
                item.put("mensaje", "DNI evaluador inválido");
                errores.add(item);
                continue;
            }
            
            // Validar DNI trabajador
            if (!dniTrabajador.matches("\\d{8}")) {
                item.put("mensaje", "DNI trabajador inválido");
                errores.add(item);
                continue;
            }
            
            // Verificar que no sean iguales
            if (dniEvaluador.equals(dniTrabajador)) {
                item.put("mensaje", "El evaluador no puede ser su propio trabajador");
                errores.add(item);
                continue;
            }
            
            // Buscar evaluador (debe existir en votante con segmento 3)
            Optional<Votante> evaluadorOpt = votanteRepository.findByIdSegmentoAndNumeroDocumento(SEGMENTO_EVALUADOR, dniEvaluador);
            if (!evaluadorOpt.isPresent()) {
                item.put("mensaje", "DNI evaluador no es un evaluador registrado");
                errores.add(item);
                continue;
            }
            
            Votante evaluador = evaluadorOpt.get();
            item.put("nombreEvaluador", (evaluador.getNombres() + " " + evaluador.getApellidos()).trim());
            
            // Buscar trabajador en usuario
            Usuario usuarioTrabajador = usuarioRepository.findDocumento(dniTrabajador);
            if (usuarioTrabajador == null) {
                item.put("mensaje", "DNI trabajador no encontrado en el sistema");
                errores.add(item);
                continue;
            }
            
            item.put("nombreTrabajador", (usuarioTrabajador.getNombres() + " " + usuarioTrabajador.getApellidos()).trim());
            
            // Verificar si trabajador ya tiene evaluador
            Optional<Votante> trabajadorOpt = votanteRepository.findByNumeroDocumento(dniTrabajador);
            if (trabajadorOpt.isPresent()) {
                Equipo equipoExistente = equipoRepository.findEvaluadorByTrabajador(trabajadorOpt.get().getIdVotante());
                if (equipoExistente != null) {
                    item.put("yaAsignado", true);
                    item.put("evaluadorActual", equipoExistente.getJefe().getNumeroDocumento());
                    item.put("nombreEvaluadorActual", 
                            (equipoExistente.getJefe().getNombres() + " " + equipoExistente.getJefe().getApellidos()).trim());
                    item.put("mensaje", "Ya tiene evaluador: " + equipoExistente.getJefe().getNumeroDocumento());
                } else {
                    item.put("yaAsignado", false);
                }
            } else {
                item.put("yaAsignado", false);
            }
            
            validos.add(item);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalFilas", filas.size());
        response.put("totalValidos", validos.size());
        response.put("totalErrores", errores.size());
        response.put("validos", validos);
        response.put("errores", errores);
        
        return response;
    }

    /**
     * Confirmar carga masiva de asignaciones
     */
    @Transactional("transactionManager1")
    public Map<String, Object> confirmarCargaMasivaTrabajadores(List<String[]> filas) {
        log.info("Confirmando carga masiva de trabajadores: {} filas", filas.size());
        
        int procesados = 0;
        int asignados = 0;
        int yaAsignados = 0;
        int errores = 0;
        
        for (String[] fila : filas) {
            if (fila.length < 2) {
                errores++;
                continue;
            }
            
            String dniEvaluador = fila[0].trim();
            String dniTrabajador = fila[1].trim();
            
            try {
                // Obtener evaluador
                Optional<Votante> evaluadorOpt = votanteRepository.findByIdSegmentoAndNumeroDocumento(SEGMENTO_EVALUADOR, dniEvaluador);
                if (!evaluadorOpt.isPresent()) {
                    errores++;
                    continue;
                }
                
                Votante evaluador = evaluadorOpt.get();
                
                // Obtener o crear trabajador
                Optional<Votante> trabajadorOpt = votanteRepository.findByNumeroDocumento(dniTrabajador);
                Votante trabajador;
                
                if (trabajadorOpt.isPresent()) {
                    trabajador = trabajadorOpt.get();
                    
                    // Verificar si ya tiene evaluador
                    Equipo equipoExistente = equipoRepository.findEvaluadorByTrabajador(trabajador.getIdVotante());
                    if (equipoExistente != null) {
                        yaAsignados++;
                        procesados++;
                        continue;
                    }
                } else {
                    // Crear votante
                    Usuario usuario = usuarioRepository.findDocumento(dniTrabajador);
                    if (usuario == null) {
                        errores++;
                        continue;
                    }
                    
                    int maxId = equipoRepository.getCantidadRegistro();
                    trabajador = new Votante();
                    trabajador.setIdVotante(maxId + 1);
                    trabajador.setNumeroDocumento(usuario.getNumeroDocumento());
                    trabajador.setNombres(usuario.getNombres());
                    trabajador.setApellidos(usuario.getApellidos());
                    trabajador.setIdSegmento(2);
                    trabajador.setIdUsuario((int) usuario.getIdUsuario());
                    votanteRepository.save(trabajador);
                }
                
                // Crear asignación
                Equipo equipo = new Equipo();
                equipo.setJefe(evaluador);
                equipo.setIntegrante(trabajador);
                equipo.setEsActivo(true);
                equipoRepository.save(equipo);
                
                asignados++;
                procesados++;
                
            } catch (Exception e) {
                log.error("Error en fila {}: {}", fila[0] + "|" + fila[1], e.getMessage());
                errores++;
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalProcesados", procesados);
        response.put("totalAsignados", asignados);
        response.put("totalYaAsignados", yaAsignados);
        response.put("totalErrores", errores);
        response.put("exito", errores == 0);
        
        log.info("Carga masiva completada: {} procesados, {} asignados, {} ya asignados, {} errores",
                procesados, asignados, yaAsignados, errores);
        
        return response;
    }
}
