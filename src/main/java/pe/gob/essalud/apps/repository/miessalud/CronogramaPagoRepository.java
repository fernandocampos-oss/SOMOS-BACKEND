package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.CronogramaPago;

import java.util.List;

public interface CronogramaPagoRepository extends JpaRepository<CronogramaPago, Integer> {

    List<CronogramaPago> findAllByOrderByTipoContratoIdTipoContratoAscPeriodoPagoIdPeriodoPagoAsc();

}
