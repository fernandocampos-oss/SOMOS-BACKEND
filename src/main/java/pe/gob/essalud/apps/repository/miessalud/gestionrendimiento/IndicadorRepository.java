package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface IndicadorRepository extends JpaRepository<Indicador, Integer> {

    @Query(value = "SELECT * from indicador i WHERE i.id_votante=? AND i.id_prioridad=? ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<Indicador> getListIndicadoresByUsuarioAndPrioridad(@Param("idVotante") int idVotante, @Param("idPrioridad") int idPrioridad);

    @Query(value = "SELECT * from indicador i WHERE i.id_prioridad=? ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<Indicador> getListIndicadoresByPrioridad(@Param("idPrioridad") int idPrioridad);

    @Query(value = "SELECT SUM(i.peso) FROM indicador i WHERE i.anio=? and i.id_votante=? ", nativeQuery = true)
    Optional<Integer> sumaTotalPesoAllIndicadorByTrabajador(@Param("anioActual") int anioActual, @Param("idVotante") int idVotante);

    @Query(value = "SELECT DISTINCT id_votante from indicador i WHERE i.anio =:anio AND i.cod_red IN (:listCodRed) AND i.cod_unidad =:codUnidad", nativeQuery = true)
    List<Integer> reporteMatrizGdrFindVontates(@Param("anio") int anio, @Param("listCodRed") ArrayList<String> listCodRed, @Param("codUnidad") String codUnidad);

    @Query(value = "SELECT i.descripcion from indicador i WHERE i.id_votante=? ORDER BY i.id_indicador ASC ", nativeQuery = true)
    List<String> listIndicadorDescripcionByVotante(@Param("idVotante") int idVotante);

    @Query(value = "SELECT * from indicador i WHERE i.id_votante=? FETCH FIRST 1 ROWS ONLY ", nativeQuery = true)
    Indicador getIndicadorByVotante(@Param("idVotante") int idVotante);
}
