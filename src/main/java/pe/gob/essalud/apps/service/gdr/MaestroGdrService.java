package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.MaestroGdr;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.gdr.MaestroGdrRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaestroGdrService {

    private final MaestroGdrRepository maestroGdrRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;

    /**
     * Verificar si el usuario actual es Maestro GDR activo
     */
    public boolean esMaestroGdr() {
        String dniActual = obtenerDniUsuarioActual();
        if (dniActual == null) {
            return false;
        }
        return maestroGdrRepository.existsByNumeroDocumentoAndEstadoTrue(dniActual);
    }

    /**
     * Verificar si un DNI específico es Maestro GDR activo
     */
    public boolean esMaestroGdrPorDni(String dni) {
        return maestroGdrRepository.existsByNumeroDocumentoAndEstadoTrue(dni);
    }

    /**
     * Listar todos los Maestros GDR activos
     */
    public List<MaestroGdr> listarActivos() {
        return maestroGdrRepository.findByEstadoTrueOrderByFechaCreacionDesc();
    }

    /**
     * Listar todos los Maestros GDR (activos e inactivos)
     */
    public List<MaestroGdr> listarTodos() {
        return maestroGdrRepository.findAllByOrderByFechaCreacionDesc();
    }

    /**
     * Agregar un nuevo Maestro GDR por DNI
     */
    @Transactional("gdrTransactionManager")
    public MaestroGdr agregar(String dni) {
        log.info("Agregando Maestro GDR con DNI: {}", dni);
        
        Optional<MaestroGdr> existente = maestroGdrRepository.findByNumeroDocumento(dni);
        
        if (existente.isPresent()) {
            MaestroGdr maestro = existente.get();
            if (!maestro.getEstado()) {
                maestro.setEstado(true);
                log.info("Reactivando Maestro GDR existente: {}", dni);
                return maestroGdrRepository.save(maestro);
            }
            log.info("Maestro GDR ya existe y está activo: {}", dni);
            return maestro;
        }
        
        MaestroGdr nuevo = new MaestroGdr();
        nuevo.setNumeroDocumento(dni);
        nuevo.setEstado(true);
        nuevo.setUsuarioCreacion(authService.getIdUserSession());
        
        MaestroGdr guardado = maestroGdrRepository.save(nuevo);
        log.info("Maestro GDR creado con ID: {}", guardado.getIdMaestroGdr());
        return guardado;
    }

    /**
     * Desactivar un Maestro GDR por DNI (no permite desactivarse a sí mismo)
     */
    @Transactional("gdrTransactionManager")
    public boolean desactivar(String dni) {
        log.info("Desactivando Maestro GDR con DNI: {}", dni);
        
        String dniActual = obtenerDniUsuarioActual();
        if (dni.equals(dniActual)) {
            log.warn("Intento de auto-desactivación bloqueado para DNI: {}", dni);
            throw new RuntimeException("No puede desactivarse a sí mismo como Maestro GDR");
        }
        
        int result = maestroGdrRepository.desactivarPorDni(dni);
        log.info("Resultado desactivación: {}", result > 0);
        return result > 0;
    }

    /**
     * Activar un Maestro GDR por DNI
     */
    @Transactional("gdrTransactionManager")
    public boolean activar(String dni) {
        log.info("Activando Maestro GDR con DNI: {}", dni);
        int result = maestroGdrRepository.activarPorDni(dni);
        log.info("Resultado activación: {}", result > 0);
        return result > 0;
    }

    /**
     * Obtener Maestro GDR por DNI
     */
    public Optional<MaestroGdr> obtenerPorDni(String dni) {
        return maestroGdrRepository.findByNumeroDocumento(dni);
    }

    /**
     * Contar Maestros GDR activos
     */
    public long contarActivos() {
        return maestroGdrRepository.countByEstadoTrue();
    }

    /**
     * Obtener DNI del usuario actual
     */
    public String obtenerDniUsuarioActual() {
        try {
            int idUsuario = authService.getIdUserSession();
            Optional<Usuario> usuarioOpt = usuarioRepository.findById((long) idUsuario);
            return usuarioOpt.map(Usuario::getNumeroDocumento).orElse(null);
        } catch (Exception e) {
            log.error("Error obteniendo DNI del usuario actual: {}", e.getMessage());
            return null;
        }
    }
}
