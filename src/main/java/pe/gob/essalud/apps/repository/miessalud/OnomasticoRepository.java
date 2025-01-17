package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import java.util.List;

public interface OnomasticoRepository extends JpaRepository<Onomastico, Integer> {

    List<Onomastico> findByMes(String mes);
    List<Onomastico> findByMesAndDia(String mes, String dia);

    @Query("SELECT u from Usuario u WHERE u.numeroDocumento=:numDoc and u.esActivo=true ")
    Usuario findUsuarioByNumDocAndEstado(@Param("numDoc") String numDoc);
}
