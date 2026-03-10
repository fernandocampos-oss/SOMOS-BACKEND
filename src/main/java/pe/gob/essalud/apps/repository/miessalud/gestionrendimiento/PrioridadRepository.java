package pe.gob.essalud.apps.repository.miessalud.gestionrendimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.EvaluadorResponseDto;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Prioridad;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PrioridadRepository extends JpaRepository<Prioridad, Integer> {

    @Query(value = "SELECT * from prioridad p WHERE p.anio=? and p.estado = true and p.id_prioridad IN (SELECT DISTINCT id_prioridad from indicador i WHERE i.id_votante=? AND i.estado = true) ORDER BY p.id_prioridad ASC ", nativeQuery = true)
    List<Prioridad> getListIdPrioridadesByTrabajador(@Param("anioActual") Number anioActual, @Param("idTrabajador") Number idTrabajador);

    @Query(value = "SELECT u.id_usuario as idUsuario, u.nombres as nombres, u.apellidos as apellidos, u.cargo as puesto, u.cod_unidad as unidad, u.numero_documento as numeroDocumento, u.correo as email, u.sexo as genero, u.fecha_nacimiento as fechaNacimiento, u.regimen as regimen FROM usuario u WHERE u.id_usuario=:idUsuario ", nativeQuery = true)
    EvaluadorResponseDto findUsuarioById(@Param("idUsuario") int idUsuario);

    @Query("SELECT uo FROM UnidadOrganizativa uo WHERE uo.codUnidad = :cod")
    UnidadOrganizativa getUnidadByCod(@Param("cod") String cod);

    @Query("SELECT i FROM Indicador i WHERE i.anio =:anio AND i.codRed IN (:listCodRed) AND i.codUnidad =:codUnidad")
    List<Indicador> reporteSeguimientoGdr(@Param("anio") int anio, @Param("listCodRed") ArrayList<String> listCodRed, @Param("codUnidad") String codUnidad);

}
