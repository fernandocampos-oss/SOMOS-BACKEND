package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.LiderEquipo;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoIngreso;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.TipoValorMeta;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.LiderEquipoRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoUsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.RequerimientoUsuarioService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequerimientoUsuarioServiceImpl implements RequerimientoUsuarioService {

    private final RequerimientoUsuarioRepository requerimientoUsuarioRepository;
    private final AuthService authService;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;
    private final LiderEquipoRepository liderEquipoRepository;

    @Override
    public List<RequerimientoUsuario> listar() {
        return null;
    }

    @Override
    public RequerimientoUsuario registrar(RequerimientoUsuario obj) {
        return null;
    }

    @Override
    public List<RequerimientoUsuario> listarRequerimientosIntegrantesPrincipal() {
        long idLider = authService.getIdUserSession();
        List<LiderEquipo> listIntegrantes = new ArrayList<>();
        listIntegrantes = liderEquipoRepository.listarIntegrantesPorLider(idLider);

        List<RequerimientoUsuario> getRequerimientosAllIntegrantesPorLider =new ArrayList<>();

        List<Long> arrayIdIntegrantes = new ArrayList<Long>();
        for (LiderEquipo integrante : listIntegrantes) {
            arrayIdIntegrantes.add(integrante.getIntegrante().getIdUsuario());
		}
        getRequerimientosAllIntegrantesPorLider = requerimientoUsuarioRepository.listarRequerimientosIntegrantesPrincipal(arrayIdIntegrantes);
        return getRequerimientosAllIntegrantesPorLider;
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

//    @Override
//    public List<RequerimientoUsuario> listarRequerimientosRechazadoPorUsuario() {
//        int idUsuario = authService.getIdUserSession();
//        return requerimientoUsuarioRepository.listarRequerimientosRechazadoPorUsuario(idUsuario);
//    }

    @Override
    public int aprobarRequerimiento(Number estado, Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.aprobarRequerimiento(estado, idRequerimientoUsuario);
    }

//    @Override
//    public int rechazarRequerimiento(Number estado, String motivo, Number idRequerimientoUsuario) {
//        return requerimientoUsuarioRepository.rechazarRequerimiento(estado, motivo, idRequerimientoUsuario);
//    }

    @Override
    public int derivarRequerimiento(Number estado, String motivo, String codUnidadReceptor, Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.derivarRequerimiento(estado, motivo, codUnidadReceptor, idRequerimientoUsuario);
    }

    @Override
    public List<UnidadOrganizativa> listarUnidad() {
        return unidadOrganizativaRepository.findAll();
    }

    @Override
    public List<RequerimientoUsuario> listarRequerimientosPorPersonal(Number idUsuario) {
        return requerimientoUsuarioRepository.listarRequerimientosPorPersonal(idUsuario);
    }
//
    @Override
    public List<PersonalDTO> listarPersonalPorRed() {
        String codRed =  authService.getCodRedSession();
        return requerimientoUsuarioRepository.listarPersonalPorRed(codRed);
    }

    @Override
    public int finalizarTareaAdministrador(Number idRequerimientoUsuario) {
        return requerimientoUsuarioRepository.finalizarTareaAdministrador(LocalDateTime.now(ZoneId.of("America/Lima")), idRequerimientoUsuario);
    }

    @Override
    public List<RequerimientoUsuario> getAllRequerimientoUsuarioPorAnio(Number anioRegistro) {
        return requerimientoUsuarioRepository.getAllRequerimientoUsuarioPorAnio(anioRegistro);
    }

    @Override
    public List<TipoValorMeta> listarAllTipoValorMeta() {
        return requerimientoUsuarioRepository.listarAllTipoValorMeta();
    }

}
