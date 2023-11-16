package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.PrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.util.List;

public interface PrioridadService {

    List<MainDto> getPrioridadPorTrabajadorEnGestionJefe();

    List<Indicador> getAllIndicadorOrganizar();

    void actualizarPrioridadEnListaIndicadores(PrioridadDto prioridadDto);

    List<Actividad> getAllActividades();

//    int finalizarTareaAdministrador(Number idRequerimientoUsuario);

    List<ExcelDto> generarExcelDirectivo();

}
