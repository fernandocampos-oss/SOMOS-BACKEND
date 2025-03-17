package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.GdrParametro;

public interface ParametroService {

    GdrParametro obtenerParametros();
    void actualizarParametros(Integer id, GdrParametro request);

}
