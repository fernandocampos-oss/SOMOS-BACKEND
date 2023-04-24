package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.Onomastico;

import java.util.List;

public interface OnomasticoService {

    List<Onomastico> findAllOnomasticos();
    List<Onomastico> findAllOnomasticosByMes(String mes);
    List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia);

}
