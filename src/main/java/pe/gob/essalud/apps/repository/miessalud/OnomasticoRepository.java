package pe.gob.essalud.apps.repository.miessalud;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.essalud.apps.model.miessalud.Onomastico;

import java.util.List;

public interface OnomasticoRepository extends JpaRepository<Onomastico, Integer> {

    List<Onomastico> findByMes(String mes);
    List<Onomastico> findByMesAndDia(String mes, String dia);

}
