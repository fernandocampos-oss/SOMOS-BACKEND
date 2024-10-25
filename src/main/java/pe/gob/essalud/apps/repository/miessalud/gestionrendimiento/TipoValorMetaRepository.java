package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoValorMeta;

import java.util.List;

public interface TipoValorMetaRepository extends JpaRepository<TipoValorMeta, Integer> {
    List<TipoValorMeta> findAllByEstado(Boolean estado);
}
