package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.PagoHistorialActividad;

import java.util.List;
import java.util.Optional;

public interface PagoHistorialActividadRepository extends JpaRepository<PagoHistorialActividad, Integer> {

    Optional<PagoHistorialActividad> findByTipoAccionAndUsuarioCreacion(int tipoAccion, int usuarioCreacion);
    List<PagoHistorialActividad> findByUsuarioCreacionOrderByIdPagoHistorial(int usuarioCreacion);

}
