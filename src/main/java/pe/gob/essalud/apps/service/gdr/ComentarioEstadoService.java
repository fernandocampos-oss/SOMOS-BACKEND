package pe.gob.essalud.apps.service.gdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.gdr.ComentarioEstado;
import pe.gob.essalud.apps.repository.gdr.ComentarioEstadoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComentarioEstadoService {

    private final ComentarioEstadoRepository comentarioEstadoRepository;

    // Guardar o actualizar comentario estado con tipo
    @Transactional("gdrTransactionManager")
    public ComentarioEstado guardarOActualizar(Long idEvidencia, String tipoComentario, String estadoDropdown, String comentarioAdicional) {
        String tipo = tipoComentario != null ? tipoComentario : "individual";
        Optional<ComentarioEstado> existente = comentarioEstadoRepository.findByIdEvidenciaAndTipoComentario(idEvidencia, tipo);

        if (existente.isPresent()) {
            // Actualizar
            ComentarioEstado comentarioEstado = existente.get();
            comentarioEstado.setEstadoDropdown(estadoDropdown);
            comentarioEstado.setComentarioAdicional(comentarioAdicional);
            return comentarioEstadoRepository.save(comentarioEstado);
        } else {
            // Crear nuevo
            ComentarioEstado nuevo = new ComentarioEstado(idEvidencia, tipo, estadoDropdown, comentarioAdicional);
            return comentarioEstadoRepository.save(nuevo);
        }
    }

    // Mantener método anterior para compatibilidad (usa 'individual' por defecto)
    @Transactional("gdrTransactionManager")
    public ComentarioEstado guardarOActualizar(Long idEvidencia, String estadoDropdown, String comentarioAdicional) {
        return guardarOActualizar(idEvidencia, "individual", estadoDropdown, comentarioAdicional);
    }

    // Obtener comentario por ID de evidencia y tipo
    public Optional<ComentarioEstado> obtenerPorIdEvidenciaYTipo(Long idEvidencia, String tipoComentario) {
        return comentarioEstadoRepository.findByIdEvidenciaAndTipoComentario(idEvidencia, tipoComentario);
    }

    // Obtener comentario por ID de evidencia (solo individuales para compatibilidad)
    public Optional<ComentarioEstado> obtenerPorIdEvidencia(Long idEvidencia) {
        return comentarioEstadoRepository.findByIdEvidenciaAndTipoComentario(idEvidencia, "individual");
    }

    // Obtener múltiples comentarios con tipo
    public Map<Long, Map<String, Object>> obtenerMultiplesPorTipo(List<Long> idsEvidencia, String tipoComentario) {
        List<ComentarioEstado> comentarios = comentarioEstadoRepository.findByIdEvidenciaInAndTipoComentario(idsEvidencia, tipoComentario);
        
        Map<Long, Map<String, Object>> resultado = new HashMap<>();
        for (ComentarioEstado comentario : comentarios) {
            Map<String, Object> info = new HashMap<>();
            info.put("estadoDropdown", comentario.getEstadoDropdown());
            info.put("comentarioAdicional", comentario.getComentarioAdicional());
            info.put("tipoComentario", comentario.getTipoComentario());
            resultado.put(comentario.getIdEvidencia(), info);
        }
        
        return resultado;
    }

    // Obtener múltiples comentarios (todos los tipos)
    public Map<Long, Map<String, Object>> obtenerMultiples(List<Long> idsEvidencia) {
        List<ComentarioEstado> comentarios = comentarioEstadoRepository.findByIdEvidenciaIn(idsEvidencia);
        
        Map<Long, Map<String, Object>> resultado = new HashMap<>();
        for (ComentarioEstado comentario : comentarios) {
            // Usar clave compuesta: idEvidencia + "_" + tipo para distinguir registros
            String clave = comentario.getIdEvidencia() + "_" + comentario.getTipoComentario();
            Map<String, Object> info = new HashMap<>();
            info.put("estadoDropdown", comentario.getEstadoDropdown());
            info.put("comentarioAdicional", comentario.getComentarioAdicional());
            info.put("tipoComentario", comentario.getTipoComentario());
            info.put("idEvidencia", comentario.getIdEvidencia());
            resultado.put(comentario.getIdEvidencia(), info);
        }
        
        return resultado;
    }

    // Obtener todos los comentarios (ambos tipos) como lista
    public Map<String, Map<String, Object>> obtenerTodosMultiples(List<Long> idsEvidencia) {
        List<ComentarioEstado> comentarios = comentarioEstadoRepository.findByIdEvidenciaIn(idsEvidencia);
        
        Map<String, Map<String, Object>> resultado = new HashMap<>();
        for (ComentarioEstado comentario : comentarios) {
            // Usar clave compuesta: idEvidencia_tipo para distinguir registros
            String clave = comentario.getIdEvidencia() + "_" + comentario.getTipoComentario();
            Map<String, Object> info = new HashMap<>();
            info.put("estadoDropdown", comentario.getEstadoDropdown());
            info.put("comentarioAdicional", comentario.getComentarioAdicional());
            info.put("tipoComentario", comentario.getTipoComentario());
            info.put("idEvidencia", comentario.getIdEvidencia());
            resultado.put(clave, info);
        }
        
        return resultado;
    }

    // Eliminar comentario
    @Transactional("gdrTransactionManager")
    public void eliminarPorIdEvidencia(Long idEvidencia) {
        comentarioEstadoRepository.deleteByIdEvidencia(idEvidencia);
    }
}
