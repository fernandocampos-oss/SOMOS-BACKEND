package pe.gob.essalud.apps.repository.miessalud.encuestaformulario;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuestaTrabajador;

public interface FormEncuestaTrabajadorRepository extends JpaRepository<FormEncuestaTrabajador, Integer> {
}
