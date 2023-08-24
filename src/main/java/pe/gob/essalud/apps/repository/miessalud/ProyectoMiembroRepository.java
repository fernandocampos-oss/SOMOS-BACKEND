package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.ProyectoMiembro;

import java.util.List;

public interface ProyectoMiembroRepository extends JpaRepository<ProyectoMiembro, Integer> {

    List<ProyectoMiembro> findByDni(String dni);

}
