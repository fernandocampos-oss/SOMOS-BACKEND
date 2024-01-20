package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.EmailNotificacionRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.IndicadorRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.util.List;
import java.util.Optional;

public interface PrioridadService {

    List<MainDto> listGestionarIndicadoresPrincipalJefe();

    List<Actividad> getAllActividades();

    List<ExcelDto> generarExcelDirectivo();

    void sendCorreoNotificacion(EmailNotificacionRequestDto requestDto);

}
