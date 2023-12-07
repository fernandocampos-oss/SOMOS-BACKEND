package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IndicadorRepository extends JpaRepository<Indicador, Integer> {

    @Query(value = "SELECT * from indicador i WHERE i.id_votante=? AND i.id_prioridad=? ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<Indicador> getListIndicadoresByUsuarioAndPrioridad(@Param("idVotante") int idVotante, @Param("idPrioridad") int idPrioridad);

    @Query(value = "SELECT SUM(i.peso) FROM indicador i WHERE i.anio=? and i.id_votante=? ", nativeQuery = true)
    Optional<Integer> sumaTotalPesoAllIndicadorByTrabajador(@Param("anioActual") int anioActual, @Param("idVotante") int idVotante);

}
