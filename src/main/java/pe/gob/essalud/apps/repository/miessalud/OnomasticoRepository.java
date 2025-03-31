package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.dto.onomastico.response.IOnomasticoResponseDto;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import java.util.List;

public interface OnomasticoRepository extends JpaRepository<Onomastico, Integer> {

    List<Onomastico> findByMes(String mes);
//    List<Onomastico> findByMesAndDia(String mes, String dia);

    @Query("SELECT u from Usuario u WHERE u.numeroDocumento=:numDoc and u.esActivo=true ")
    Usuario findUsuarioByNumDocAndEstado(@Param("numDoc") String numDoc);

    @Query(value = "SELECT u.id_usuario as idUsuario, " +
            "u.nombres as nombres, " +
            "u.apellidos as apellidos, " +
            "u.correo as correo, " +
            "u.fecha_nacimiento as fechaNacimiento, " +
            "u.es_activo as esActivo, " +
            "uo.descripcion as unidadDescripcion " +
            "FROM Usuario u " +
            "INNER JOIN unidad_organizativa uo ON uo.cod_unidad = u.cod_unidad " +
            "WHERE TO_CHAR(u.fecha_nacimiento, 'MM') = :mes " +
            "AND TO_CHAR(u.fecha_nacimiento, 'DD') = :dia " +
            "AND u.es_activo = true " +
            "AND u.id_estado_usuario = '02' " +
            "ORDER BY u.apellidos", nativeQuery = true)
    List<IOnomasticoResponseDto> obtenerOnomasticosInterfazPorDiaAndEstado(@Param("mes") String mes, @Param("dia") String dia);

}
