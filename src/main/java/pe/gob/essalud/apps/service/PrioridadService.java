package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.EmailNotificacionRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdatePrioridadDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.reporteGdrRequest.ReporteSeguimientoRequestDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.ExcelDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.MainDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.reporteGdrResponse.ReporteMatrizResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.reporteGdrResponse.ReporteSeguimientoResponseDto;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;

import java.util.List;

public interface PrioridadService {

    List<MainDto> listGestionarIndicadoresPrincipalJefe();

    List<Actividad> getAllActividades();

    List<ExcelDto> generarExcelDirectivo();

    void sendCorreoNotificacion(EmailNotificacionRequestDto requestDto);

    List<UnidadOrganizativa> getAllUnidadesOrganizativas();

    List<ReporteSeguimientoResponseDto> reporteSeguimientoGdr(ReporteSeguimientoRequestDto requestDto);

    List<ReporteMatrizResponseDto> reporteMatrizGdr(ReporteSeguimientoRequestDto requestDto);

    void modificarPrioridad(int id, UpdatePrioridadDto requestDto);

    void eliminarPrioridad(int id);

}