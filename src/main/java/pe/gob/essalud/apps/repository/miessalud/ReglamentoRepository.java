package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.model.miessalud.Reglamento;

public interface ReglamentoRepository extends JpaRepository<Reglamento, Integer> {
    @Query(value = "SELECT * from reglamento r WHERE r.numero_documento=? and r.primer_semestre=1 and r.anio=? ", nativeQuery = true)
    Reglamento getReglamentoByPrimerSemestreAnio(@Param("numDoc") String numDoc, @Param("anio") int anio);

    @Query(value = "SELECT * from reglamento r WHERE r.numero_documento=? and r.segundo_semestre=2 and r.anio=? ", nativeQuery = true)
    Reglamento getReglamentoBySegundoSemestreAnio(@Param("numDoc") String numDoc, @Param("anio") int anio);
}
