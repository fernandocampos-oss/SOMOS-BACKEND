package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Indicador;

public interface IndicadorService {

    Indicador registrarIndicador(Indicador indicador);

    void modificarIndicador(Integer idIndicador, Indicador request);

}
