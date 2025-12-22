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

    // Guardar o actualizar comentario estado
    @Transactional("gdrTransactionManager")
    public ComentarioEstado guardarOActualizar(Long idEvidencia, String estadoDropdown, String comentarioAdicional) {
        Optional<ComentarioEstado> existente = comentarioEstadoRepository.findByIdEvidencia(idEvidencia);

        if (existente.isPresent()) {
            // Actualizar
            ComentarioEstado comentarioEstado = existente.get();
            comentarioEstado.setEstadoDropdown(estadoDropdown);
            comentarioEstado.setComentarioAdicional(comentarioAdicional);
            return comentarioEstadoRepository.save(comentarioEstado);
        } else {
            // Crear nuevo
            ComentarioEstado nuevo = new ComentarioEstado(idEvidencia, estadoDropdown, comentarioAdicional);
            return comentarioEstadoRepository.save(nuevo);
        }
    }

    // Obtener comentario por ID de evidencia
    public Optional<ComentarioEstado> obtenerPorIdEvidencia(Long idEvidencia) {
        return comentarioEstadoRepository.findByIdEvidencia(idEvidencia);
    }

    // Obtener múltiples comentarios
    public Map<Long, Map<String, Object>> obtenerMultiples(List<Long> idsEvidencia) {
        List<ComentarioEstado> comentarios = comentarioEstadoRepository.findByIdEvidenciaIn(idsEvidencia);
        
        Map<Long, Map<String, Object>> resultado = new HashMap<>();
        for (ComentarioEstado comentario : comentarios) {
            Map<String, Object> info = new HashMap<>();
            info.put("estadoDropdown", comentario.getEstadoDropdown());
            info.put("comentarioAdicional", comentario.getComentarioAdicional());
            resultado.put(comentario.getIdEvidencia(), info);
        }
        
        return resultado;
    }

    // Eliminar comentario
    @Transactional("gdrTransactionManager")
    public void eliminarPorIdEvidencia(Long idEvidencia) {
        comentarioEstadoRepository.deleteByIdEvidencia(idEvidencia);
    }
}
