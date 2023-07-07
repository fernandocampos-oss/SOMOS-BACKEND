package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.EvidenciaArchivo;

import javax.transaction.Transactional;

public interface EvidenciaArchivoRepository extends JpaRepository<EvidenciaArchivo, Integer> {

    @Query(value = "SELECT * from evidencia_archivo ea WHERE ea.id_evidencia_archivo=? AND ea.estado=true ", nativeQuery = true)
    EvidenciaArchivo listarArchivoPorEstadoActivo(@Param("idEvidenciaArchivo") Number idEvidenciaArchivo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE evidencia_archivo SET estado = ? WHERE id_evidencia_archivo=? ", nativeQuery = true)
    public int eliminarArchivo(@Param("estado") Boolean estado, @Param("idEvidenciaArchivo") Number idEvidenciaArchivo);

}
