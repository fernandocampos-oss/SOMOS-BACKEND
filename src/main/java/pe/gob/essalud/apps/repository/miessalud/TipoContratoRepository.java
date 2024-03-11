package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.TipoContrato;

import java.util.List;

public interface TipoContratoRepository extends JpaRepository<TipoContrato, Integer> {

    List<TipoContrato> findAllByOrderByIdTipoContratoAsc();

}
