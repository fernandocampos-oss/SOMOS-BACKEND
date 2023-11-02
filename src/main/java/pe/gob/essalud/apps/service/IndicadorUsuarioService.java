package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.IndicadorUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoValorMeta;

import java.util.List;

public interface IndicadorUsuarioService extends IcrudService<IndicadorUsuario> {

    List<IndicadorUsuario> listarRequerimientosIntegrantesPrincipal();

    List<IndicadorUsuario> listarRequerimientosPendientesPorUsuario();

    List<IndicadorUsuario> listarRequerimientosFinalizadoPorUsuario();

//    List<RequerimientoUsuario> listarRequerimientosRechazadoPorUsuario();

    int aprobarRequerimiento(Number estado, Number idRequerimientoUsuario);

//    int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario);

//    int derivarRequerimiento(Number estado, String motivo, String codUnidadReceptor, Number idRequerimientoUsuario);

//    List<UnidadOrganizativa> listarUnidad();

    List<IndicadorUsuario> listarRequerimientosPorPersonal(Number idUsuario);
//
//    List<PersonalDTO> listarPersonalPorRed();

    int finalizarTareaAdministrador(Number idRequerimientoUsuario);

    List<IndicadorUsuario> getAllRequerimientoUsuarioPorAnio(Number anioRegistro);

    List<TipoIngreso> getAllTipoIngreso();

    List<TipoValorMeta> getAllTipoValorMeta();



}
