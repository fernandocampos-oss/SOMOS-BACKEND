package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;

public interface TipoIngresoRepository extends JpaRepository<TipoIngreso, Integer> {
}
