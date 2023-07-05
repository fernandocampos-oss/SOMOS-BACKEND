package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Archivo;

public interface ArchivoRepository extends JpaRepository<Archivo, Integer> {
}
