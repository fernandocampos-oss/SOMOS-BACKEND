package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

public interface EvidenciaRepository extends JpaRepository<Evidencia, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE evidencia SET sustento_descripcion=?, sustento_ruta_file=?, sustento_extension_file=?, sustento_fecha_registro=? WHERE id_evidencia=?", nativeQuery = true)
    Integer crearEvidencia(@Param("sustentoDescripcion") String sustentoDescripcion,
                           @Param("rutaFile") String rutaFile,
                           @Param("extension") String extension,
                           @Param("sustentoFechaRegistro") LocalDateTime sustentoFechaRegistro,
                           @Param("idEvidencia") Number idEvidencia);

    @Query("SELECT t FROM Evidencia t WHERE t.indicador.idIndicador = :idIndicador ORDER BY t.idEvidencia ASC ")
    List<Evidencia> listEvidenciaByIdIndicador(@Param("idIndicador") int idIndicador);

}

