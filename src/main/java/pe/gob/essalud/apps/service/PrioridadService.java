package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.util.List;

public interface PrioridadService {

    List<MainDto> getPrioridadPorTrabajadorEnGestionJefe();

    List<Indicador> getAllIndicadorOrganizar();

//    int actualizarPrioridad(Integer idActividad, Integer idPrioridad);

    void actualizarPrioridadEnListaIndicadores(PrioridadDto prioridadDto);

    int asignarPesoPrioridad(int peso, int idPrioridad);

    List<Actividad> getAllActividades();






//
////    int aprobarIndicador(Number estado, Number idIndicadorUsuario);
////    int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario);
////    List<IndicadorUsuario> getlistIndicadoresByIdUsuario(Number idUsuario);
//
//    int finalizarTareaAdministrador(Number idRequerimientoUsuario);
//    List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(Number anioRegistro);

}
