package pe.gob.essalud.apps.repository.miessalud.encuestaformulario;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormEncuestaTrabajador;
import pe.gob.essalud.apps.model.miessalud.encuestapublicacion.FormPregunta;

import java.util.List;
import java.util.Optional;

public interface FormPreguntaRepository extends JpaRepository<FormPregunta, Integer> {

//    @Query(value = "select * from form_encuesta_trabajador WHERE id_form_encuesta=:idEncuesta ORDER BY id_form_encuesta_trabajador ASC ", nativeQuery = true)
//    List<FormEncuestaTrabajador> listarRespuestasByIdEncuesta(@Param("idEncuesta") Integer idEncuesta);

    @Query(value = "SELECT DISTINCT id_trabajador from form_encuesta_trabajador where id_form_encuesta=:idEncuesta ", nativeQuery = true)
    List<Integer> listarTrabajadoresEncuestadosPorEncuesta(@Param("idEncuesta") Integer idEncuesta);

    @Query("select ft from FormEncuestaTrabajador ft WHERE ft.idFormEncuesta=:idEncuesta AND ft.idTrabajador=:idTrabajador ")
    List<FormEncuestaTrabajador> listarRespuestasPorEncuestaYtrabajador(@Param("idEncuesta") Integer idEncuesta, @Param("idTrabajador") Integer idTrabajador);

    @Query(value = "select * from form_pregunta WHERE id_form_encuesta=:id ORDER BY id_form_pregunta ASC ", nativeQuery = true)
    List<FormPregunta> listarPreguntasByIdEncuesta(@Param("id") Integer id);

    @Query(value = "select finalizado from Form_Encuesta_Trabajador  WHERE id_Form_Encuesta=:idEncuesta AND id_Trabajador=:idTrabajador LIMIT 1 ", nativeQuery = true)
    Optional<Boolean> evaluarEncuestaFinalizado(@Param("idEncuesta") Integer idEncuesta, @Param("idTrabajador") Integer idTrabajador);

}
