package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.UnidadOrganizativa;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoUsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.RequerimientoUsuarioService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequerimientoUsuarioServiceImpl implements RequerimientoUsuarioService {

    private final RequerimientoUsuarioRepository requerimientoUsuarioRepository;
    private final AuthService authService;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;

    @Override
    public List<RequerimientoUsuario> listar() {
        return null;
    }

    @Override
    public RequerimientoUsuario registrar(RequerimientoUsuario obj) {
        return null;
    }

    @Override
    public List<RequerimientoUsuario> listarRequerimientosPrincipalPorUnidadOrganizativa() {
        String codUnidadOrganizacion= authService.getCodUnidadSession();
        return requerimientoUsuarioRepository.listarRequerimientosPrincipalPorUnidadOrganizativa(codUnidadOrganizacion);
    }

    @Override
    public List<RequerimientoUsuario> listarRequerimientosPendientesPorUsuario() {
        int idUsuario = authService.getIdUserSession();
        return requerimientoUsuarioRepository.listarRequerimientosPendientesPorUsuario(idUsuario);
    }

    @Override
    public List<RequerimientoUsuario> listarRequerimientosFinalizadoPorUsuario() {
        int idUsuario = authService.getIdUserSession();
        return requerimientoUsuarioRepository.listarRequerimientosFinalizadoPorUsuario(idUsuario);
    }

    @Override
    public List<RequerimientoUsuario> listarRequerimientosRechazadoPorUsuario() {
        int idUsuario = authService.getIdUserSession();
        return requerimientoUsuarioRepository.listarRequerimientosRechazadoPorUsuario(idUsuario);
    }

    @Override
    public int aprobarRequerimiento(Number estado, Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.aprobarRequerimiento(estado, idRequerimientoUsuario);
    }

    @Override
    public int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
    }

    @Override
    public int derivarRequerimiento(Number estado, String motivo, String codUnidadReceptor, Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.derivarRequerimiento(estado, motivo, codUnidadReceptor, idRequerimientoUsuario);
    }

    @Override
    public List<UnidadOrganizativa> listarRedes() {
        return unidadOrganizativaRepository.findAll();
    }

    @Override
    public List<PersonalDTO> listarPersonalPorUnidadOrganizacional() {
        String codUnidadOrganizacion= authService.getCodUnidadSession();
        return requerimientoUsuarioRepository.listarPersonalPorUnidadOrganizacional(codUnidadOrganizacion);
    }

    @Override
    public List<RequerimientoUsuario> listarRequerimientosPorPersonal(Number idUsuario) {
        return requerimientoUsuarioRepository.listarRequerimientosPorPersonal(idUsuario);
    }

    @Override
    public List<PersonalDTO> listarPersonalGeneral() {
        return requerimientoUsuarioRepository.listarPersonalGeneral();
    }

    @Override
    public int eliminarIntegranteUnidad(Number idUnidad) {
        return requerimientoUsuarioRepository.eliminarIntegranteUnidad(idUnidad);
    }

    @Override
    public int agregarIntegranteUnidad(Number idUnidad) {
        String codUnidad= authService.getCodUnidadSession();
        return requerimientoUsuarioRepository.agregarIntegranteUnidad(codUnidad, idUnidad);
    }

    @Override
    public int finalizarTareaAdministrador(Number idRequerimientoUsuario) {
        log.info("idRequerimientoUsuario [{}]", idRequerimientoUsuario);
        return requerimientoUsuarioRepository.finalizarTareaAdministrador(idRequerimientoUsuario);
    }

}
