package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {
}
