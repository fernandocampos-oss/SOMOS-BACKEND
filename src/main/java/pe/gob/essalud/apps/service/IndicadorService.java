package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.util.List;

public interface IndicadorService {

    Indicador registrarIndicador(Indicador indicador);

    List<Indicador> getListIndicadoresPendientesByUser();

    List<TipoIngreso> getAllTipoIngreso();

    List<TipoValorMeta> getAllTipoValorMeta();

    void modificarIndicador(Integer idIndicador, Indicador request);

//    List<Indicador> getListIndicadoresFinalizadoByUser();

    int asignarPesoIndicador(int peso, int idIndicador);

}
