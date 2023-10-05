package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;

import java.util.List;

public interface RequerimientoUsuarioService extends IcrudService<RequerimientoUsuario> {

    List<RequerimientoUsuario> listarRequerimientosIntegrantesPrincipal();

    List<RequerimientoUsuario> listarRequerimientosPendientesPorUsuario();

    List<RequerimientoUsuario> listarRequerimientosFinalizadoPorUsuario();

    List<RequerimientoUsuario> listarRequerimientosRechazadoPorUsuario();

    int aprobarRequerimiento(Number estado, Number idRequerimientoUsuario);

    int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario);

    int derivarRequerimiento(Number estado, String motivo, String codUnidadReceptor, Number idRequerimientoUsuario);

    List<UnidadOrganizativa> listarUnidad();

    List<RequerimientoUsuario> listarRequerimientosPorPersonal(Number idUsuario);

    List<PersonalDTO> listarPersonalPorRed();

    int finalizarTareaAdministrador(Number idRequerimientoUsuario);

}
