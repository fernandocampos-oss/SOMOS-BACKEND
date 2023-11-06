package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.response.GestionIndicadoresTrabajadorDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Actividad;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.IndicadorUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoValorMeta;

import java.util.List;

public interface IndicadorUsuarioService {

    List<GestionIndicadoresTrabajadorDto> listarTrabajadoresIndicadoresJefePrincipal();

    List<IndicadorUsuario> listarIndicadoresPendientesPorUsuario();

    List<IndicadorUsuario> listarIndicadoresFinalizadoPorUsuario();

//    int aprobarIndicador(Number estado, Number idIndicadorUsuario);

//    int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario);

//    List<IndicadorUsuario> getlistIndicadoresByIdUsuario(Number idUsuario);

    int finalizarTareaAdministrador(Number idRequerimientoUsuario);

    List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(Number anioRegistro);

    List<TipoIngreso> getAllTipoIngreso();

    List<TipoValorMeta> getAllTipoValorMeta();

    List<Actividad> getAllActividades();

}
